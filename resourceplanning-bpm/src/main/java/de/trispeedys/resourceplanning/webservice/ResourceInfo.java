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
import de.trispeedys.resourceplanning.interaction.HelperInteraction;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
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

    public void assignHelper(Long helperId, Long positionId, Long eventId)
    {
        AssignmentService.assignHelper(DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helperId), DatasourceRegistry.getDatasource(Event.class).findById(Event.class, eventId),
                DatasourceRegistry.getDatasource(Position.class).findById(Position.class, positionId));
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

    public void startSomeProcesses()
    {
        HibernateUtil.clearAll();

        Event event2016 = DatabaseRoutines.duplicateEvent(TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        List<Helper> helpers = DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        for (Helper helper : helpers)
        {
            startHelperRequestProcess(helper.getId(), event2016.getId());
        }
    }

    /**
     * starts a process and blocks a position in order to test {@link HelperCallback#CHANGE_POS} with an already blocked
     * position chosen.
     */
    public void prepareBlockedChoosePosition()
    {
        HibernateUtil.clearAll();

        Event event2016 = DatabaseRoutines.duplicateEvent(TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        // block one of the positions with a new helper
        Helper blockingHelper = EntityFactory.buildHelper("New1", "New1", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        AssignmentService.assignHelper(blockingHelper, event2016, (Position) DatasourceRegistry.getDatasource(Position.class).findAll(Position.class).get(0));

        // start process for the created helper 'H2_Last'
        startHelperRequestProcess(((Helper) DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_LAST_NAME, "H2_Last").get(0)).getId(), event2016.getId());
    }

    /**
     * Duplicates the smimple event and creates two new helpers. Idea:
     * 
     * + Let one of the 'old helpers' choose option {@link HelperCallback#CHANGE_POS} (receives). + Inbetween, block two
     * of the positions with the new helpers. + Old helper can either choose a free or occupied positions (which causes
     * an assignment or a second proposal mail).
     */
    public void prepareSimpleEventWithFloatingHelpers()
    {
        HibernateUtil.clearAll();

        Event event2016 = DatabaseRoutines.duplicateEvent(TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        startHelperRequestProcess(DatasourceRegistry.getDatasource(Helper.class).findAll(Helper.class).get(0).getId(), event2016.getId());

        EntityFactory.buildHelper("New1", "New1", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        EntityFactory.buildHelper("New2", "New2", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
    }

    /**
     * like the 'status quo' - example {@link ResourceInfo#startSomeProcesses()}, but with 2 new helpers which block 2
     * positions, so 2 of 5 {@link HelperCallback#ASSIGNMENT_AS_BEFORE} will not work (and alternative positions will be
     * proposed).
     */
    public void startSomeProcessesWithNewHelpers()
    {
        HibernateUtil.clearAll();

        Event event2016 = DatabaseRoutines.duplicateEvent(TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        List<Helper> helpers = DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        List<Position> positions = DatasourceRegistry.getDatasource(Position.class).findAll(Position.class);
        // new helper 1 with assignment
        Helper newHelper1 = EntityFactory.buildHelper("New1", "New1", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        AssignmentService.assignHelper(newHelper1, event2016, positions.get(1));
        // new helper 2 with assignment
        Helper newHelper2 = EntityFactory.buildHelper("New2", "New2", "a@b.de", HelperState.ACTIVE, 5, 5, 1980).persist();
        AssignmentService.assignHelper(newHelper2, event2016, positions.get(3));
        for (Helper helper : helpers)
        {
            startHelperRequestProcess(helper.getId(), event2016.getId());
        }
    }

    public void startOneProcesses()
    {
        HibernateUtil.clearAll();

        Event event2016 = DatabaseRoutines.duplicateEvent(TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        List<Helper> helpers = DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, Helper.ATTR_HELPER_STATE, HelperState.ACTIVE);
        startHelperRequestProcess(helpers.get(0).getId(), event2016.getId());
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