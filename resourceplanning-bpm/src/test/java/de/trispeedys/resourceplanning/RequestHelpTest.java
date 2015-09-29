package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
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
import de.trispeedys.resourceplanning.entity.util.DataModelUtil;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class RequestHelpTest
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
    @Test(expected = PvmException.class)
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

        Position position = EntityFactory.buildPosition("Moo", 12).persist();
        Event event = EntityFactory.buildEvent("TRI", "TRI", 21, 6, 2012).persist();
        Helper helper =
                EntityFactory.buildHelper("Stefan", "Schulz", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).persist();

        // assign position to event
        DataModelUtil.relatePositionsToEvent(event, position);

        // create preconditions (this must be a follow up assignment)
        EntityFactory.buildEventCommitment(helper, event, position).persist();

        RequestHelpTestUtil.startHelperRequestProcess(helper, event,
                ResourcePlanningUtil.generateRequestHelpBusinessKey(null, null), rule);

        // mail must have been sent
        List<MessageQueue> messages = MessagingService.findAllMessages();
        System.out.println(messages.size() + " messages found.");
        assertEquals(1, messages.size());
    }

    /**
     * Follow up assignment -> assignment wished as before (2014), but position is already occupied in 2015 (to
     * 'blocking helper').
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingAssigmnmentForPosition()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create position
        Position positionBikeEntry = EntityFactory.buildPosition("Radeinfahrt Helmkontrolle", 12).persist();
        // helper was assigned to position 'Radeinfahrt Helmkontrolle' the year before (2014)...
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 21);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.YEAR, 2014);
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
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(null, null);
        RequestHelpTestUtil.startHelperRequestProcess(createdHelper, evt2015, businessKey, rule);
        // correlate callback message
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey,
                variables);
        // now the manual new assignment must have been generated by the process engine...
    }
}