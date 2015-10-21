package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmJobDefinitions;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmSignals;
import de.trispeedys.resourceplanning.execution.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.test.TestDataGenerator;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;
import de.trispeedys.resourceplanning.util.configuration.AppConfigurationValues;

public class RequestHelpExecutionTest
{
    @Rule
    public ProcessEngineRule processEngine = new ProcessEngineRule();

    /**
     * - (0) clear database - (1) prepare event with helpers assigned for 2015 (all positions are assigned, same count
     * of positions and helpers) - (2) duplicate event (for 2016) - (3) assign the position which was assigned to helper
     * 'A' in 2015 to helper 'B' in 2016 - (4) start process for helper 'A' and event in 2016 who was assigned to that
     * position in the year before (2015) - (5) let helper 'A' state (after ingoring the first mail -->
     * 'REMINDER_STEP_0') he wants to be assigned as before (by sending message 'MSG_HELP_CALLBACK' with paramter
     * {@link HelperCallback#ASSIGNMENT_AS_BEFORE}) - (6) check availability must fail as the position is already
     * assigned (to 'B') - (7) mail with other positions (4) must be generated and sent to 'A' - (8) helper 'A' chooses
     * one of that positions ('C') (by triggering message 'MSG_POS_CHOSEN' with position id as variable) - (9) chosen
     * position ('C') must be assigned to the helper 'A' afterwards - (10) process must be finished (after signal for
     * the event start)
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testProposePositionsOnAssignmentRequestedAsBefore()
    {
        // (0)
        HibernateUtil.clearAll();

        // (1)
        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);

        // (2)
        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        // (3)
        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);
        // get assigned position for helper 'A' in 2015
        HelperAssignment assignmentA2015 = AssignmentService.getHelperAssignments(helperA, event2015).get(0);
        AssignmentService.assignHelper(helperB, event2016, assignmentA2015.getPosition());

        // (4)
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        // (5)
        RequestHelpTestUtil.fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_HELPER_REMINDER_TIMER,
                processEngine);

        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, businessKey, processEngine);

        // (6) --> if CheckAvailabiliy-Delegate fails, variable 'posAvailable' must be set to 'false'
        // ??? how to check the variable ???

        // (7) --> mails with types 'MessagingType.REMINDER_STEP_0' and 'MessagingType.PROPOSE_POSITIONS' must be
// there...
        List<Position> unassignedPositionsIn2016 = PositionService.findUnassignedPositionsInEvent(event2016);
        assertEquals(4, unassignedPositionsIn2016.size());
        assertTrue(RequestHelpTestUtil.checkMails(3, MessagingType.REMINDER_STEP_0,
                MessagingType.REMINDER_STEP_1, MessagingType.PROPOSE_POSITIONS));

        // (8)
        Position chosenPosition = unassignedPositionsIn2016.get(1);
        RequestHelpTestUtil.choosePosition(businessKey, chosenPosition, processEngine, event2016.getId());

        // (9)
        // ...
        List<HelperAssignment> helperAssignmentA2016 =
                AssignmentService.getHelperAssignments(helperA, event2016);
        assertEquals(1, helperAssignmentA2016.size());

        // (10)
        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }

    /**
     * Like {@link RequestHelpExecutionTest#testProposePositionsOnAssignmentRequestedAsBefore()}, but the position
     * helper ('A') was assigned to last year before is available (not blocked by helper 'B'). This means that 'A' must
     * be automatically assigned to his used position by the system.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testAssignmentRequestedAsBeforeSuccesful()
    {
        HibernateUtil.clearAll();

        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);

        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);

        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        RequestHelpTestUtil.doCallback(HelperCallback.ASSIGNMENT_AS_BEFORE, businessKey, processEngine);

        List<HelperAssignment> helperAssignmentA2016 =
                AssignmentService.getHelperAssignments(helperA, event2016);
        assertEquals(1, helperAssignmentA2016.size());

        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }

    /**
     * tests the deactivation of a helper not respondig to any reminder mail, but in the end, he responds positive to
     * the 'last chance' mail (which means that he remains {@link HelperState#ACTIVE}).
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNotCooperativeAndRecoveredHelper()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'little' event for 2015
        Long eventId2015 = TestDataGenerator.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015).getId();
        // duplicate event
        Event event2016 = SpeedyRoutines.duplicateEvent(eventId2015, "TRI-2016", "TRI-2016", 21, 6, 2015);
        // start request process for every helper
        List<Helper> activeHelpers =
                Datasources.getDatasource(Helper.class).find("helperState",
                        HelperState.ACTIVE);
        Helper notCooperativeHelper = activeHelpers.get(0);
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(notCooperativeHelper.getId(),
                        event2016.getId());

        RequestHelpTestUtil.doNotRespondToAnything(event2016, notCooperativeHelper, businessKey,
                processEngine);

        // answer to mail (i do not want to be deactivated)
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_DEACT_RESP,
                businessKey);

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // helper state remains 'ACTIVE'
        assertEquals(
                HelperState.ACTIVE,
                ((Helper) Datasources.getDatasource(Helper.class).findById(notCooperativeHelper.getId())).getHelperState());
    }

    /**
     * Like {@link RequestHelpExecutionTest#testNotCooperativeAndRecoveredHelper()}, but the helper does NOT respond to
     * the last chance mail so that the one month timer is fired. That means that the helper gets deactived.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testCompletetlyUncooperativeHelper()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create 'little' event for 2015
        Long eventId2015 = TestDataGenerator.createSimpleEvent("TRI-2015", "TRI-2015", 21, 6, 2015).getId();
        // duplicate event
        Event event2016 = SpeedyRoutines.duplicateEvent(eventId2015, "TRI-2016", "TRI-2016", 21, 6, 2015);
        // start request process for every helper
        List<Helper> activeHelpers =
                Datasources.getDatasource(Helper.class).find("helperState",
                        HelperState.ACTIVE);
        Helper notCooperativeHelper = activeHelpers.get(0);
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(notCooperativeHelper.getId(),
                        event2016.getId());

        RequestHelpTestUtil.doNotRespondToAnything(event2016, notCooperativeHelper, businessKey,
                processEngine);

        // fire the 'last chance' timer
        RequestHelpTestUtil.fireTimer(BpmJobDefinitions.RequestHelpHelper.JOB_DEF_LAST_CHANCE_TIMER,
                processEngine);

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // helper state changes to 'INACTIVE'
        assertEquals(
                HelperState.INACTIVE,
                ((Helper) Datasources.getDatasource(Helper.class).findById(notCooperativeHelper.getId())).getHelperState());
    }

    /**
     * Tests {@link HelperCallback#PAUSE_ME}.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testPauseMe()
    {
        // clear all tables in db
        HibernateUtil.clearAll();

        HibernateUtil.clearAll();

        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);

        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);

        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        Map<String, Object> variablesCallback = new HashMap<String, Object>();
        variablesCallback.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.PAUSE_ME);
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK,
                businessKey, variablesCallback);

        // process must be gone, helper must remain at state active
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
        assertEquals(
                HelperState.ACTIVE,
                ((Helper) Datasources.getDatasource(Helper.class).findById(helperA.getId())).getHelperState());
    }

    /**
     * Testing a process fpr a helper who wants to change his position (start like
     * {@link RequestHelpExecutionTest#testProposePositionsOnAssignmentRequestedAsBefore}, but the helper commits the
     * message for {@link HelperCallback#CHANGE_POS}) (A). He then chooses a positions which already has been assigned
     * to someone else (B), so he gets a second mail, choose a free position this time (C). Then the position is
     * assigned to 'helperA' and the process is gone after signal (D).
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testChangePositions()
    {
        HibernateUtil.clearAll();

        Event event2015 = TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015);

        Event event2016 =
                SpeedyRoutines.duplicateEvent(event2015.getId(), "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        List<Helper> allHelpers = Datasources.getDatasource(Helper.class).findAll();
        assertEquals(5, allHelpers.size());
        Helper helperA = allHelpers.get(1);
        Helper helperB = allHelpers.get(3);

        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKey, processEngine);

        // (A)
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKey, processEngine);

        // check mails ('REMINDER_STEP_0' und 'PROPOSE_POSITIONS' must be there)
        assertTrue(RequestHelpTestUtil.checkMails(2, MessagingType.REMINDER_STEP_0,
                MessagingType.PROPOSE_POSITIONS));

        // (B) we assign one of the proposed prosition to another helper and let 'helperA' choose it...
        Position blockedPosition = PositionService.findUnassignedPositionsInEvent(event2016).get(0);
        Position notBlockedPosition = PositionService.findUnassignedPositionsInEvent(event2016).get(1);
        AssignmentService.assignHelper(helperB, event2016, blockedPosition);
        RequestHelpTestUtil.choosePosition(businessKey, blockedPosition, processEngine, event2016.getId());

        // (C) --> there must be a second proposal mail
        assertTrue(RequestHelpTestUtil.checkMails(3, MessagingType.REMINDER_STEP_0,
                MessagingType.PROPOSE_POSITIONS));
        RequestHelpTestUtil.choosePosition(businessKey, notBlockedPosition, processEngine, event2016.getId());

        // (D)
        assertEquals(1, AssignmentService.getHelperAssignments(helperA, event2016).size());
        processEngine.getRuntimeService().signalEventReceived(BpmSignals.RequestHelpHelper.SIG_EVENT_STARTED);
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());
    }

    /**
     * Helper 'A' chooses {@link HelperCallback#CHANGE_POS} and gets the correlating mail. Before choosing a position,
     * it gets blocked by helper 'B' (running the same process), so 'A' gets another mail with proposed positions.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testPositionBlockedOnChoosePosition()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        List<Helper> helpers = Datasources.getDatasource(Helper.class).findAll();

        Helper helperA = helpers.get(0);
        String businessKeyA =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helperA.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperA, event2016, businessKeyA, processEngine);
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKeyA, processEngine);

        Helper helperB = helpers.get(2);
        String businessKeyB =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helperB.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helperB, event2016, businessKeyB, processEngine);
        RequestHelpTestUtil.doCallback(HelperCallback.CHANGE_POS, businessKeyB, processEngine);

        List<Position> allUnassignedPositions = PositionService.findUnassignedPositionsInEvent(event2016);
        Position desiredPosition = allUnassignedPositions.get(2);

        // 'B' is faster...
        RequestHelpTestUtil.choosePosition(businessKeyB, desiredPosition, processEngine, event2016.getId());

        // 'B' is booked for desired position...
        assertEquals(
                1,
                Datasources.getDatasource(HelperAssignment.class)
                        .find(HelperAssignment.ATTR_EVENT, event2016,
                                HelperAssignment.ATTR_HELPER, helperB, HelperAssignment.ATTR_POSITION,
                                desiredPosition)
                        .size());

        // 'A' comes to late
        RequestHelpTestUtil.choosePosition(businessKeyA, desiredPosition, processEngine, event2016.getId());

        // 'A' has two mails of type 'PROPOSE_POSITIONS'...
        assertEquals(
                2,
                Datasources.getDatasource(MessageQueue.class)
                        .find(MessageQueue.ATTR_MESSAGING_TYPE,
                                MessagingType.PROPOSE_POSITIONS, MessageQueue.ATTR_TO_ADDRESS,
                                helperA.getEmail())
                        .size());
    }

    /**
     * A new helper is manually assigned, but after being assigned to a position, he cancels his assignment. That for,
     * an alert mail must be sent ({@link AppConfigurationValues#ADMIN_MAIL}) and the assignment must have the status
     * {@link HelperAssignmentState#CANCELLED}.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNewHelperCancelsAssignment()
    {
        HibernateUtil.clearAll();

        Event event2016 =
                SpeedyRoutines.duplicateEvent(
                        TestDataGenerator.createSimpleEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).getId(),
                        "Triathlon 2016", "TRI-2016", 21, 6, 2016);

        // new helper
        Helper helper =
                EntityFactory.buildHelper("Mee", "Moo", "a@b.de", HelperState.ACTIVE, 23, 6, 2000).persist();

        // start process
        String businessKey =
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event2016.getId());
        RequestHelpTestUtil.startHelperRequestProcess(helper, event2016, businessKey, processEngine);

        // manual assignment task must be there
        assertTrue(RequestHelpTestUtil.wasTaskGenerated(
                BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, processEngine));

        // finish it (with a position)
        Task task =
                processEngine.getTaskService()
                        .createTaskQuery()
                        .taskDefinitionKey(
                                BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT)
                        .list()
                        .get(0);
        HashMap<String, Object> variables = new HashMap<String, Object>();
        Position someUnassignedTask =
                (Position) Datasources.getDatasource(Position.class).findAll().get(0);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, someUnassignedTask.getId());
        processEngine.getTaskService().complete(task.getId(), variables);

        // Send cancellation message
        processEngine.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_ASSIG_CANCELLED,
                businessKey);

        // process must be gone
        assertEquals(0, processEngine.getRuntimeService().createExecutionQuery().list().size());

        // check canncelled assignment
        assertEquals(
                HelperAssignmentState.CANCELLED,
                ((HelperAssignment) Datasources.getDatasource(HelperAssignment.class)
                        .find(HelperAssignment.ATTR_EVENT, event2016,
                                HelperAssignment.ATTR_HELPER, helper)
                        .get(0))
                        .getHelperAssignmentState());

        // check admin mail
        // TODO
    }
}