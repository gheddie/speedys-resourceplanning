package de.trispeedys.resourceplanning.util;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.test.ProcessEngineRule;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperState;
import de.trispeedys.resourceplanning.entity.builder.HelperBuilder;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class RequestHelpTestUtil
{
    /**
     * Starts process with helper and for follow up assignment (firstAssignment -> false).
     * 
     * @param rule
     * @param businessKey 
     */
    public static void startProcessForFollowinAssignment(ProcessEngineRule rule, String businessKey)
    {
        Helper helper =
                new HelperBuilder().withFirstName("Klaus")
                        .withLastName("Meier")
                        .withEmail("testhelper1.trispeedys@gmail.com")
                        .withHelperState(HelperState.ACTIVE)
                        .build()
                        .persist();

        // start process for follow up assignment
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_FIRST_ASSIGNMENT, false);
        variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_ID, new Long(helper.getId()));
        rule.getRuntimeService().startProcessInstanceByMessage(BpmMessages.RequestHelpHelper.MSG_HELP_TRIG, businessKey, variables);
    }
}