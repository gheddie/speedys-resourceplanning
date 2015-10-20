package de.trispeedys.resourceplanning.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.camunda.bpm.BpmPlatform;

import de.trispeedys.resourceplanning.dto.HelperAssignmentDTO;
import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.interaction.EventManager;
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.MessagingService;

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
        Helper helper = Datasources.getDatasource(Helper.class).findById(helperId);
        Event event = Datasources.getDatasource(Event.class).findById(eventId);
        Position position = Datasources.getDatasource(Position.class).findById(positionId);
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
    
    public void startProcessesForActiveHelpers(String templateName)
    {
        EventManager.triggerHelperProcesses(templateName);
    }
    
    public void finishUp()
    {
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
    }
}