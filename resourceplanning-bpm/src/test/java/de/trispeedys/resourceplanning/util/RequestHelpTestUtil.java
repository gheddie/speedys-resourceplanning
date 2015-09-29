package de.trispeedys.resourceplanning.util;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.ProcessEngineRule;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.builder.EntityBuilder;
import de.trispeedys.resourceplanning.entity.builder.HelperBuilder;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class RequestHelpTestUtil
{
    /**
     * Starts process with helper and for follow up assignment (firstAssignment -> false).
     * 
     * @param helper
     * @param rule
     * @param businessKey
     */
    public static void startProcessForFollowinAssignment(Helper helper, Event event,
            ProcessEngineRule rule, String businessKey)
    {
        // start process for follow up assignment
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        variables.put(BpmVariables.RequestHelpHelper.VAR_EVENT_ID, new Long(event.getId()));
        rule.getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG,
                businessKey, variables);
    }

    public static Helper createHelper()
    {
        return new HelperBuilder().withFirstName("Klaus")
                .withLastName("Meier")
                .withEmail("testhelper1.trispeedys@gmail.com")
                .withHelperState(HelperState.ACTIVE)
                .build()
                .persist();
    }

    public static Event createEvent()
    {
        return EntityBuilder.buildEvent("Tirathlon 2015", "TRI-2014", 21, 6, 2014).persist();
    }
}