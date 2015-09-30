package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.pvm.PvmException;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.misc.SpeedyTestUtil;
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.misc.GenericBpmTest;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.tasks.BpmTaskDefinitionKeys;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class RequestHelpTest extends GenericBpmTest
{
    private static final String KEY_HELPER_PROCESS = "RequestHelpHelperProcess";

    private static final Helper DEFAULT_HELPER = EntityFactory.buildHelper("Stefan", "Schulz", "", HelperState.ACTIVE,
            13, 2, 1976).persist();

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testParsingAndDeployment()
    {
        // ...
    }

    @Test
    public void testBusinessKeyGeneration()
    {
        assertEquals("bkRequestHelpHelperProcess_helper:123||event:456",
                ResourcePlanningUtil.generateRequestHelpBusinessKey(new Long(123), new Long(456)));
    }

    /**
     * trying to start process without passing a business key
     */
    @Test(expected = ResourcePlanningException.class)
    @Deployment(resources = "RequestHelp.bpmn")
    public void testStartWithoutBusinessKey()
    {
        // clear db
        HibernateUtil.clearAll();
        // ...
        Event event = EntityFactory.buildEvent("", "", 1, 1, 2000).persist();
        RequestHelpTestUtil.startHelperRequestProcess(DEFAULT_HELPER, event, null, rule);
    }

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testReceiveReminderMail()
    {
        HibernateUtil.clearAll();

        Position position = EntityFactory.buildPosition("Moo", 12, SpeedyTestUtil.buildDefaultDomain()).persist();
        Event event = EntityFactory.buildEvent("TRI", "TRI", 21, 6, 2012).persist();
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).persist();

        // assign position to event
        DataModelUtil.relatePositionsToEvent(event, position);

        // create preconditions (this must be a follow up assignment)
        EntityFactory.buildEventCommitment(helper, event, position).persist();

        RequestHelpTestUtil.startHelperRequestProcess(helper, event,
                ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), event.getId()), rule);

        // mail must have been sent
        List<MessageQueue> messages = MessagingService.findAllMessages();
        System.out.println(messages.size() + " messages found.");
        assertEquals(1, messages.size());
    }

    /**
     * For a helper proposed for TRI 2016 without prior assignments, the user task for manual assignment must be
     * generated instantly.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFirstAssigmnment()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create event
        Event evt2016 = EntityFactory.buildEvent("Triathlon 2016", "TRI-2016", 21, 6, 2016).persist();
        // create helper
        Helper helper = EntityFactory.buildHelper("Stefan", "Schulz", "", HelperState.ACTIVE, 1, 1, 1990).persist();
        // start process
        RequestHelpTestUtil.startHelperRequestProcess(helper, evt2016, ResourcePlanningUtil.generateRequestHelpBusinessKey(helper.getId(), evt2016.getId()), rule);
        // check
        assertTrue(wasTaskGenerated(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, rule));
    }

    /**
     * Follow up assignment -> assignment wished as before (2014), but position is already occupied in 2015 (to
     * 'blocking helper'). In this case, the user task for manual assignment must be generated.
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingAssigmnmentForPosition()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create position
        Position positionBikeEntry = EntityFactory.buildPosition("Radeinfahrt Helmkontrolle", 12, SpeedyTestUtil.buildDefaultDomain()).persist();
        // create events
        Event evt2014 = EntityFactory.buildEvent("Triathlon 2014", "TRI-2014", 21, 6, 2014).persist();
        Event evt2015 = EntityFactory.buildEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).persist();
        // create helper
        Helper createdHelper =
                EntityFactory.buildHelper("Stefan", "Schulz", "", HelperState.ACTIVE, 1, 1, 1990).persist();
        Helper blockingHelper =
                EntityFactory.buildHelper("Blocking", "Helper", "", HelperState.ACTIVE, 1, 1, 1990).persist();
        // assign position to event
        DataModelUtil.relateEventsToPosition(positionBikeEntry, evt2014, evt2015);
        // assign helper to position in 2014
        EntityFactory.buildEventCommitment(createdHelper, evt2014, positionBikeEntry).persist();
        // assign position to another helper in 2015
        EntityFactory.buildEventCommitment(blockingHelper, evt2015, positionBikeEntry).persist();
        // start request process for 2015...
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(createdHelper.getId(), evt2015.getId());
        RequestHelpTestUtil.startHelperRequestProcess(createdHelper, evt2015, businessKey, rule);
        // correlate callback message
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey,
                variables);
        // now the manual new assignment must have been generated by the process engine,
        // as the desired position is blocked by 'blocking helper' in 2015...
        assertTrue(wasTaskGenerated(BpmTaskDefinitionKeys.RequestHelpHelper.TASK_DEFINITION_KEY_MANUAL_ASSIGNMENT, rule));
    }
}