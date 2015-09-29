package de.trispeedys.resourceplanning.util;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.ProcessEngineRule;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class RequestHelpTestUtil
{
    /**
     * Starts process with helper and for follow up assignment with given helper id, event id and business key.
     * 
     * @param helper
     * @param businessKey
     * @param rule
     */
    public static void startHelperRequestProcess(Helper helper, Event event,
            String businessKey, ProcessEngineRule rule)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(event.getId()));
        rule.getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }
}