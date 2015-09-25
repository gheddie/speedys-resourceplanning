package de.trispeedys.resourceplanning;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.pvm.PvmException;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import de.trispeedys.resourceplanning.entity.HelperCallback;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.logic.MessagingHelper;
import de.trispeedys.resourceplanning.messages.BpmMessages;
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
        //...
    }
    
    /**
     * trying to start process without passing a business key
     */
    @Test(expected = PvmException.class)
    @Deployment(resources = "RequestHelp.bpmn")
    public void testStartWithoutBusinessKey()
    {
        HibernateUtil.clearAll();

        RequestHelpTestUtil.startProcessForFollowinAssignment(rule, null);
    }

    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testReceiveReminderMail()
    {
        HibernateUtil.clearAll();

        RequestHelpTestUtil.startProcessForFollowinAssignment(rule, ResourcePlanningUtil.generateRequestHelpBusinessKey());

        // mail must have been sent
        List<MessageQueue> messages = MessagingHelper.findAllMessages();
        System.out.println(messages.size() + " messages found.");
        assertEquals(1, messages.size());
    }
    
    /**
     * Follow up assignment -> assignment wished as before,
     * but position is already occupied
     */
    @Test
    @Deployment(resources = "RequestHelp.bpmn")
    public void testNewAssigmnmentForPosition()
    {
        HibernateUtil.clearAll();

        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey();
        
        RequestHelpTestUtil.startProcessForFollowinAssignment(rule, businessKey);
                
        //correlate callback message
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
        rule.getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
    }
}