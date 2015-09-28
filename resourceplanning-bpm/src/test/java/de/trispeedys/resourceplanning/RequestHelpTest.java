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

import de.trispeedys.resourceplanning.entity.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperCallback;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.builder.EventCommitmentBuilder;
import de.trispeedys.resourceplanning.entity.builder.EventOccurenceBuilder;
import de.trispeedys.resourceplanning.entity.builder.PositionBuilder;
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

    /**
     * trying to start process without passing a business key
     */
    @Test(expected = PvmException.class)
    @Deployment(resources = "RequestHelp.bpmn")
    public void testStartWithoutBusinessKey()
    {
        HibernateUtil.clearAll();

        RequestHelpTestUtil.startProcessForFollowinAssignment(RequestHelpTestUtil.createHelper(), rule, null);
    }

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testReceiveReminderMail()
    {
        HibernateUtil.clearAll();

        RequestHelpTestUtil.startProcessForFollowinAssignment(RequestHelpTestUtil.createHelper(), rule,
                ResourcePlanningUtil.generateRequestHelpBusinessKey());

        // mail must have been sent
        List<MessageQueue> messages = MessagingService.findAllMessages();
        System.out.println(messages.size() + " messages found.");
        assertEquals(1, messages.size());
    }

    /**
     * Follow up assignment -> assignment wished as before, but position is already occupied
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testFollowingAssigmnmentForPosition()
    {
        HibernateUtil.clearAll();

        // helper was assigned to position 'Radeinfahrt Helmkontrolle' the year before...
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 21);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.YEAR, 2014);
        EventOccurence occurence =
                new EventOccurenceBuilder().withDate(cal.getTime())
                        .withDescription("Triathlon 2014")
                        .withEventKey("TRI-2014")
                        .build()
                        .persist();
        Helper createdHelper = RequestHelpTestUtil.createHelper();
        Position pos =
                new PositionBuilder().withDescription("Radeinfahrt Helmkontrolle").withMinimalAge(12).build().persist();
        new EventCommitmentBuilder().withCommitmentState(EventCommitmentState.CONFIRMED)
                .withEventOccurence(occurence)
                .withHelper(createdHelper)
                .withPosition(pos)
                .build()
                .persist();

        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey();

        RequestHelpTestUtil.startProcessForFollowinAssignment(createdHelper, rule, businessKey);

        // correlate callback message
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey,
                variables);
    }
}