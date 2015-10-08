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
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.test.DatabaseRoutines;
import de.trispeedys.resourceplanning.test.TestDataProvider;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.variables.BpmVariables;

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

    public void startHelperRequestProcess(Long helperId, Long eventId)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helperId));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(eventId));
        BpmPlatform.getDefaultProcessEngine()
                .getRuntimeService()
                .startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG, ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId), variables);
    }

    @SuppressWarnings("unchecked")
    public void startSomeProcesses()
    {
        HibernateUtil.clearAll();
        
        Event event2016 =
                DatabaseRoutines.duplicateEvent(
                        TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        List<Helper> helpers =
                DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE,
                        HelperState.ACTIVE);
        for (Helper helper : helpers)
        {
            startHelperRequestProcess(helper.getId(), event2016.getId());
        }
    }
    
    @SuppressWarnings("unchecked")
    public void startOneProcesses()
    {
        HibernateUtil.clearAll();
        
        Event event2016 =
                DatabaseRoutines.duplicateEvent(
                        TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        List<Helper> helpers =
                DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE,
                        HelperState.ACTIVE);
        startHelperRequestProcess(helpers.get(0).getId(), event2016.getId());
    }
    
    public void sendAllMessages()
    {
        MessagingService.sendAllUnprocessedMessages();
    }
}