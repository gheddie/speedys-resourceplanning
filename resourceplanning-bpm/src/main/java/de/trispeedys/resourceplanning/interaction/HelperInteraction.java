package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.MismatchingMessageCorrelationException;

import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.execution.BpmMessages;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.ResourcePlanningUtil;

public class HelperInteraction
{
    /**
     * Quittung für korrekt zugestellte Nachricht
     */
    private static final String RETURN_MESSAGE_PROCESSABLE = "RETURN_MESSAGE_PROCESSABLE";

    /**
     * Catch-All-Quittung für beide Fälle ({@link BpmMessages.RequestHelpHelper#MSG_HELP_CALLBACK} und
     * {@link BpmMessages.RequestHelpHelper#MSG_POS_CHOSEN}) --> Nachricht nicht zustellbar
     */
    private static final String RETURN_MESSAGE_UNPROCESSABLE = "RETURN_MESSAGE_UNPROCESSABLE";

    /**
     * Quittung für korrekt zugestellte Nachricht {@link BpmMessages.RequestHelpHelper#MSG_POS_CHOSEN} --> Positiv-Fall --> Position verfügbar
     */
    private static final String RETURN_POS_CHOSEN_NOMINAL = "RETURN_POS_CHOSEN_NOMINAL";

    /**
     * Quittung für korrekt zugestellte Nachricht {@link BpmMessages.RequestHelpHelper#MSG_POS_CHOSEN} --> Negativ-Fall --> Position NICHT verfügbar
     */
    private static final String RETURN_POS_CHOSEN_POS_TAKEN = "POS_CHOSEN_POS_TAKEN";

    /**
     * called from 'HelperCallbackReceiver.jsp'
     * 
     * @param eventId
     * @param helperId
     * @param callback
     */
    public static String processReminderCallback(Long eventId, Long helperId, HelperCallback callback)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        return processReminderCallback(callback, businessKey);
    }

    public static String processReminderCallback(HelperCallback callback, String businessKey)
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
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.CHANGE_POS);
                break;
            case PAUSE_ME:
                LoggerService.log("the helper wants to be paused...", DbLogLevel.INFO);
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.PAUSE_ME);
                break;
        }
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
            return RETURN_MESSAGE_PROCESSABLE;
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return RETURN_MESSAGE_UNPROCESSABLE;
        }
    }

    /**
     * called from 'ChosenPositionReceiver.jsp'
     * 
     * @param eventId
     * @param helperId
     * @param chosenPositionId
     * @param positionAvailable
     * @param request
     * 
     * @throws MismatchingMessageCorrelationException
     */
    public static String processPositionChosenCallback(Long eventId, Long helperId, Long chosenPositionId)
            throws MismatchingMessageCorrelationException
    {
        boolean positionAvailable = PositionService.isPositionAvailable(eventId, chosenPositionId);
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        // TODO catch message correlation mismtach and generate error code
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
            if (positionAvailable)
            {
                return RETURN_POS_CHOSEN_NOMINAL;
            }
            else
            {
                // inform user about position taken + generation of additional mail ('PROPOSE_POSITIONS')
                return RETURN_POS_CHOSEN_POS_TAKEN;
            }
        }
        catch (Exception e)
        {
            return RETURN_MESSAGE_UNPROCESSABLE;
        }
    }
}