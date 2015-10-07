package de.trispeedys.resourceplanning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.jobs.BpmJobDefinitions;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.CommitmentService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.DatabaseRoutines;
import de.trispeedys.resourceplanning.test.TestDataProvider;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.variables.BpmVariables;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestHelpExecutionTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();
    
    /**
     * - (0) clear database
     * - (1) prepare event with helpers assigned for 2015 (all positions are assigned, same count of positions and helpers)
     * - (2) duplicate event (for 2016)
     * - (3) assign the position which was assigned to helper 'A' in 2015 to helper 'B' in 2016
     * - (4) start process for helper 'A' and event in 2016 who was assigned to that position in the year before (2015)
     * - (5) let helper 'A' state (after ingoring the first mail --> 'REMINDER_STEP_0') he wants to be assigned as before (by sending message 'MSG_HELP_CALLBACK' with paramter {@link HelperCallback#ASSIGNMENT_AS_BEFORE})
     * - (6) check availability must fail as the position is already assigned (to 'B')
     * - (7) mail with other positions (4) must be generated and sent to 'A'
     * - (8) helper 'A' chooses one of that positions ('C') (by triggering message 'MSG_POS_CHOSEN' with position id as variable)
     * - (9) chosen position ('C') must be assigned to the helper 'A' afterwards
     * - (10) process must be finished
     */
    @SuppressWarnings("unchecked")
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testProposePositionsOnAssignmentRequestedAsBefore()
    {
        // (0)
        HibernateUtil.clearAll();
        
        // (1)
        Event event2015 = TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        
        // (2)
        Event event2016 = DatabaseRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        // (3)
        List<Helper> allHelpers = DatasourceRegistry.getDatasource(Helper.class).findAll(Helper.class);
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);
        //get assigned position for helper 'A' in 2015
        EventCommitment commitmentA2015 = CommitmentService.getHelperCommitments(helperA, event2015).get(0);
        CommitmentService.confirmHelper(helperB, event2016, commitmentA2015.getPosition());
        
        // (4)
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);
        
        // (5)
        processEngine.getManagementService().executeJob(
                processEngine.getManagementService()
                        .createJobQuery()
                        .activityId(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER)
                        .list()
                        .get(0)
                        .getId());
        Map<String, Object> variablesCallback = new HashMap<String, Object>();
        variablesCallback.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variablesCallback);
        
        // (6) --> if CheckAvailabiliy-Delegate fails, variable 'posAvailable' must be set to 'false'
        // ??? how to check the variable ???
        
        // (7) --> mails with types 'MessagingType.REMINDER_STEP_0' and 'MessagingType.PROPOSE_POSITIONS' must be there...
        List<Position> unassignedPositionsIn2016 = PositionService.findUnassignedPositionsInEvent(event2016);
        assertEquals(4, unassignedPositionsIn2016.size());
        assertTrue(RequestHelpTestUtil.checkMails(3, MessagingType.REMINDER_STEP_0, MessagingType.REMINDER_STEP_1, MessagingType.PROPOSE_POSITIONS));
        
        // (8)
        Position chosenPosition = unassignedPositionsIn2016.get(1);
        Map<String, Object> variablesPosChosen = new HashMap<String, Object>();
        variablesPosChosen.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPosition.getId());
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variablesPosChosen);
        
        // (9)        
        // ...
        List<EventCommitment> commitmentsA2016 = CommitmentService.getHelperCommitments(helperA, event2016);
        assertEquals(1, commitmentsA2016.size());
        
        // (10)
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }
    
    /**
     * Like {@link RequestHelpExecutionTest#testProposePositionsOnAssignmentRequestedAsBefore()}, but the position helper ('A')
     * was assigned to last year before is available (not blocked by helper 'B'). This means that 'A' must be automatically
     * assigned to his used position by the system.
     */
    @SuppressWarnings("unchecked")
    //@Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testAssignmentRequestedAsBeforeSuccesful()
    {
        HibernateUtil.clearAll();
        
        Event event2015 = TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        
        Event event2016 = DatabaseRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        List<Helper> allHelpers = DatasourceRegistry.getDatasource(Helper.class).findAll(Helper.class);
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);
        
        Map<String, Object> variablesCallback = new HashMap<String, Object>();
        variablesCallback.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variablesCallback);
        
        List<EventCommitment> commitmentsA2016 = CommitmentService.getHelperCommitments(helperA, event2016);
        assertEquals(1, commitmentsA2016.size());
        
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }
}