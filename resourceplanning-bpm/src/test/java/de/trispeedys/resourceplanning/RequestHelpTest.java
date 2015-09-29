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
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.entity.misc.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.MessagingService;
import de.trispeedys.resourceplanning.util.RequestHelpTestUtil;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class RequestHelpTest
{
    private static final String KEY_HELPER_PROCESS = "RequestHelpHelperProcess";

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
        HibernateUtil.clearAll();

        RequestHelpTestUtil.startProcessForFollowinAssignment(RequestHelpTestUtil.createHelper(),
                RequestHelpTestUtil.createEvent(), rule, null);
    }

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testReceiveReminderMail()
    {
        HibernateUtil.clearAll();

        Position position = EntityBuilder.buildPosition("Moo", 12).persist();
        Event event = EntityBuilder.buildEvent("TRI", "TRI", 21, 6, 2012).persist();
        Helper helper =
                EntityBuilder.buildHelper("Stefan", "Schulz", "a@b.de", HelperState.ACTIVE, 13, 2, 1976).persist();
        // create preconditions (this must be a follow up assignment)
        EntityBuilder.buildEventCommitment(helper, event, position, EventCommitmentState.CONFIRMED).persist();

        RequestHelpTestUtil.startProcessForFollowinAssignment(helper, event, rule,
                ResourcePlanningUtil.generateRequestHelpBusinessKey(null, null));

        // mail must have been sent
        List<MessageQueue> messages = MessagingService.findAllMessages();
        System.out.println(messages.size() + " messages found.");
        assertEquals(1, messages.size());
    }

    /**
     * Follow up assignment -> assignment wished as before (2014),
     * but position is already occupied (in 2015).
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingAssigmnmentForPosition()
    {
        // clear all tables in db
        HibernateUtil.clearAll();
        // create position
        Position pos = EntityBuilder.buildPosition("Radeinfahrt Helmkontrolle", 12).persist();
        // helper was assigned to position 'Radeinfahrt Helmkontrolle' the year before (2014)...
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 21);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.YEAR, 2014);
        // create events
        Event evt2014 = EntityBuilder.buildEvent("Triathlon 2014", "TRI-2014", 21, 6, 2014).persist();
        Event evt2015 = EntityBuilder.buildEvent("Triathlon 2015", "TRI-2015", 21, 6, 2015).persist();
        // create helper
        Helper createdHelper = RequestHelpTestUtil.createHelper();
        EntityBuilder.buildEventCommitment(createdHelper, evt2014, pos, EventCommitmentState.CONFIRMED).persist();
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(null, null);
        //start request process for 2015...
        RequestHelpTestUtil.startProcessForFollowinAssignment(createdHelper, evt2015, rule, businessKey);
        // correlate callback message
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey,
                variables);
    }
}