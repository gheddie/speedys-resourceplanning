package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.camunda.bpm.BpmPlatform;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.dto.HelperAssignmentDTO;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.test.SpeedyRoutines;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;

@SuppressWarnings("restriction")
@WebService
@SOAPBinding(style = Style.RPC)
public class ResourceInfo
{
    public HelperAssignmentDTO[] queryHelperAssignments(String firstName, String lastName)
    {
        List<HelperAssignmentDTO> helperAssignmentList = new ArrayList<HelperAssignmentDTO>();
        return helperAssignmentList.toArray(new HelperAssignmentDTO[helperAssignmentList.size()]);
    }

    public void assignHelper(Long helperId, Long positionId, Long eventId)
    {
        Helper helper = DatasourceRegistry.getDatasource(Helper.class).findById(helperId);
        Event event = DatasourceRegistry.getDatasource(Event.class).findById(eventId);
        Position position = DatasourceRegistry.getDatasource(Position.class).findById(positionId);
        AssignmentService.assignHelper(helper, event,
                position);
    }

    public void sendAllMessages()
    {
        MessagingService.sendAllUnprocessedMessages();
    }

    public void processHelperCallback(String businessKey, String callback)
    {
        if ((businessKey == null) || (businessKey.length() == 0))
        {
            System.out.println("business key must be set --> returning.");
            return;
        }
        HelperCallback callbackValue = HelperCallback.valueOf(callback);
        if ((callback == null) || (callback.length() == 0) || (callbackValue == null))
        {
            System.out.println("string '' can not be interpreted as helper callback --> returning.");
            return;
        }
        LoggerService.log("processed helper callback '" + callbackValue + "' for business key '" + businessKey + "'...", DbLogLevel.INFO);
        HelperInteraction.processReminderCallback(callbackValue, businessKey);
    }
}