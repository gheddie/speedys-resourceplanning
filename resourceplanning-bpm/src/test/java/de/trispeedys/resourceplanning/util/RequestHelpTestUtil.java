package de.trispeedys.resourceplanning.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.test.ProcessEngineRule;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessageQueue;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class RequestHelpTestUtil
{
    public static final String PROCESS_DEFINITION_KEY_HELPER_PROCESS = "RequestHelpHelperProcess";

    public static final String PROCESS_DEFINITION_KEY_SYSTEM_PROCESS = "RequestHelpSystemProcess";

    /**
     * Starts process with helper and for follow up assignment with given helper id, event id and business key.
     * 
     * @param helper
     * @param businessKey
     * @param rule
     */
    public static void startHelperRequestProcess(Helper helper, Event event, String businessKey, ProcessEngineRule rule)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(event.getId()));
        rule.getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }

    public static void startTriggerHelperProcess(ProcessEngineRule rule)
    {
        rule.getRuntimeService().startProcessInstanceByKey(PROCESS_DEFINITION_KEY_SYSTEM_PROCESS);
    }

    @SuppressWarnings("unchecked")
    public static int countMails()
    {
        return DatasourceRegistry.getDatasource(MessageQueue.class).findAll(MessageQueue.class).size();
    }

    @SuppressWarnings("unchecked")
    public static boolean checkMails(int expectedMailCount, MessagingType... types)
    {
        List<MessageQueue> messages = DatasourceRegistry.getDatasource(MessageQueue.class).findAll(MessageQueue.class);
        if ((messages == null) || (messages.size() != expectedMailCount))
        {
            return false;
        }
        // all expected mail types must be there...
        for (MessagingType type : types)
        {
            if (!(messageTypePresent(type, messages)))
            {
                   return false;
            }
        }
        // finally...
        return true;
    }

    private static boolean messageTypePresent(MessagingType type, List<MessageQueue> messages)
    {
        for (MessageQueue q : messages)
        {
            if (q.getMessagingType().equals(type))
            {
                return true;
            }
        }
        // type is not there...
        return false;
    }
}