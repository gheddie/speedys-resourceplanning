package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;

import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.messages.BpmMessages;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class HelperInteraction
{
    public static void processCallback(HelperCallback callback, Long eventId, Long helperId)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        processCallback(callback, businessKey);
    }
    
    public static void processCallback(HelperCallback callback, String businessKey)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        switch (callback)
        {
            case ASSIGNMENT_AS_BEFORE:
                LoggerService.log("the helper wants to be assigned as before...", DbLogLevel.INFO);
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK,
                        HelperCallback.ASSIGNMENT_AS_BEFORE);
                break;
            case CHANGE_POS:
                LoggerService.log("the helper wants to change positions...", DbLogLevel.INFO);
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK,
                        HelperCallback.CHANGE_POS);
                break;
            case PAUSE_ME:
                LoggerService.log("the helper wants to be paused...", DbLogLevel.INFO);
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK,
                        HelperCallback.PAUSE_ME);
                break;
        }
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey,
                            variables);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            throw new ResourcePlanningException("Nachricht bereits zugestellt.");
        }
    }    
}