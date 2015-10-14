package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.jobs.BpmJobDefinitions;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.DatabaseRoutines;
import de.trispeedys.resourceplanning.test.TestDataProvider;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.variables.BpmVariables;

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
        HelperAssignment assignmentA2015 = AssignmentService.getHelperAssignments(helperA, event2015).get(0);
        AssignmentService.assignHelper(helperB, event2016, assignmentA2015.getPosition());
        
        // (4)
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);
        
        // (5)
        RequestHelpTestUtil.fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER, processEngine);

        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, businessKey, processEngine);
        
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
        List<HelperAssignment> helperAssignmentA2016 = AssignmentService.getHelperAssignments(helperA, event2016);
        assertEquals(1, helperAssignmentA2016.size());
        
        // (10)
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }
    
    /**
     * Like {@link RequestHelpExecutionTest#testProposePositionsOnAssignmentRequestedAsBefore()}, but the position helper ('A')
     * was assigned to last year before is available (not blocked by helper 'B'). This means that 'A' must be automatically
     * assigned to his used position by the system.
     */
    @SuppressWarnings("unchecked")
    @Test
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
        
        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, businessKey, processEngine);
        
        List<HelperAssignment> helperAssignmentA2016 = AssignmentService.getHelperAssignments(helperA, event2016);
        assertEquals(1, helperAssignmentA2016.size());
        
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }
    
    /**
     * tests the deactivation of a helper not respondig to any reminder mail, but in the end, he responds positive to
     * the 'last chance' mail (which means that he remains {@link HelperState#ACTIVE}).
     */
    @SuppressWarnings("unchecked")
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNotCooperativeAndRecoveredHelper()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'little' event for 2015
        Long eventId2015 = TestDataProvider.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015).getId();
        // duplicate event
        Event event2016 = DatabaseRoutines.duplicateEvent(eventId2015, "TRI-2016", "TRI-2016", 21, 6, 2015);
        // start request process for every helper
        List<Helper> activeHelpers =
                DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, "helperState", HelperState.ACTIVE);
        Helper notCooperativeHelper = activeHelpers.get(0);
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(notCooperativeHelper.getId(), event2016.getId());
        
        RequestHelpTestUtil.doNotRespondToAnything(event2016, notCooperativeHelper, businessKey, processEngine);

        // answer to mail (i do not want to be deactivated)
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_DEACT_RESP, businessKey);

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
        
        // helper state remains 'ACTIVE'
        assertEquals(HelperState.ACTIVE, ((Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, notCooperativeHelper.getId())).getHelperState());
    }  
    
    /**
     * Like {@link RequestHelpExecutionTest#testNotCooperativeAndRecoveredHelper()}, but the helper does NOT respond to the last chance mail
     * so that the one month timer is fired. That means that the helper gets deactived.
     */
    @SuppressWarnings("unchecked")
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testCompletetlyUncooperativeHelper()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'little' event for 2015
        Long eventId2015 = TestDataProvider.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015).getId();
        // duplicate event
        Event event2016 = DatabaseRoutines.duplicateEvent(eventId2015, "TRI-2016", "TRI-2016", 21, 6, 2015);
        // start request process for every helper
        List<Helper> activeHelpers =
                DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, "helperState", HelperState.ACTIVE);
        Helper notCooperativeHelper = activeHelpers.get(0);
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(notCooperativeHelper.getId(), event2016.getId());
        
        RequestHelpTestUtil.doNotRespondToAnything(event2016, notCooperativeHelper, businessKey, processEngine);
        
        // fire the 'last chance' timer
        RequestHelpTestUtil.fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_LAST_CHANCE_TIMER, processEngine);

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
        
        // helper state remains 'ACTIVE'
        assertEquals(HelperState.INACTIVE, ((Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, notCooperativeHelper.getId())).getHelperState());
    }
    
    /**
     * Tests {@link HelperCallback#PAUSE_ME}.
     */
    @SuppressWarnings("unchecked")
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testPauseMe()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        
        HibernateUtil.clearAll();
        
        Event event2015 = TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        
        Event event2016 = DatabaseRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        List<Helper> allHelpers = DatasourceRegistry.getDatasource(Helper.class).findAll(Helper.class);
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);
        
        Map<String, Object> variablesCallback = new HashMap<String, Object>();
        variablesCallback.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.PAUSE_ME);
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variablesCallback);
        
        //process must be gone, helper must remain at state active
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
        assertEquals(HelperState.ACTIVE, ((Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helperA.getId())).getHelperState());
    }
    
    /**
     * Testing a process fpr a helper who wants to change his position (start like {@link RequestHelpExecutionTest#testProposePositionsOnAssignmentRequestedAsBefore}, 
     * but the helper commits the message for {@link HelperCallback#CHANGE_POS}) (A). He then chooses a positions which already has been assigned to someone else (B),
     * so he gets a second mail, choose a free position this time (C). Then the process is gone (D).
     */
    @SuppressWarnings("unchecked")
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testChangePositions()
    {
        HibernateUtil.clearAll();
        
        Event event2015 = TestDataProvider.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);
        
        Event event2016 = DatabaseRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);
        
        List<Helper> allHelpers = DatasourceRegistry.getDatasource(Helper.class).findAll(Helper.class);
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);
        //get assigned position for helper 'A' in 2015
        HelperAssignment assignmentA2015 = AssignmentService.getHelperAssignments(helperA, event2015).get(0);
        AssignmentService.assignHelper(helperB, event2016, assignmentA2015.getPosition());
        
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);
        
        // (A)
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKey, processEngine);
        
        //check mails ('REMINDER_STEP_0' und 'PROPOSE_POSITIONS' must be there)
        assertTrue(RequestHelpTestUtil.checkMails(2, MessagingType.REMINDER_STEP_0, MessagingType.PROPOSE_POSITIONS));
        
        // (B)
        // ...
        
        // (C)
        // ...
        
        // (D)
        // ...
    }
}