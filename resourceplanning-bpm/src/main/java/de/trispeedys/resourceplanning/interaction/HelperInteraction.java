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
     * Quittung f�r korrekt zugestellte Nachricht
     */
    public static final String RETURN_MESSAGE_PROCESSABLE = "RETURN_MESSAGE_PROCESSABLE";

    /**
     * Catch-All-Quittung f�r beide F�lle ({@link BpmMessages.RequestHelpHelper#MSG_HELP_CALLBACK} und
     * {@link BpmMessages.RequestHelpHelper#MSG_POS_CHOSEN}) --> Nachricht nicht zustellbar
     */
    public static final String RETURN_MESSAGE_UNPROCESSABLE = "RETURN_MESSAGE_UNPROCESSABLE";

    /**
     * Quittung f�r korrekt zugestellte Nachricht {@link BpmMessages.RequestHelpHelper#MSG_POS_CHOSEN} --> Positiv-Fall --> Position verf�gbar
     */
    public static final String RETURN_POS_CHOSEN_NOMINAL = "RETURN_POS_CHOSEN_NOMINAL";

    /**
     * Quittung f�r korrekt zugestellte Nachricht {@link BpmMessages.RequestHelpHelper#MSG_POS_CHOSEN} --> Negativ-Fall --> Position NICHT verf�gbar
     */
    public static final String RETURN_POS_CHOSEN_POS_TAKEN = "POS_CHOSEN_POS_TAKEN";
    
    /**
     * Quittung f�r korrekt zugestellte Nachricht {@link BpmMessages.RequestHelpHelper#MSG_ASSIG_CANCELLED}
     */
    public static final String RETURN_CANCELLATION_NOMINAL = "RETURN_CANCELLATION_NOMINAL";

    /**
     * 
     * 
     * @param callback
     * @param businessKey
     * @return
     */
    public static String processReminderCallback(Long eventId, Long helperId, HelperCallback callback)
    {
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
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
            return HtmlRenderer.renderCorrelationSuccess(helperId);
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return HtmlRenderer.renderCorrelationFault(helperId);
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
        try
        {
            BpmPlatform.getDefaultProcessEngine()
                    .getRuntimeService()
                    .correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
            if (positionAvailable)
            {
                // inform the user about position assignment success
                return HtmlRenderer.renderChosenPositionAvailableCallback(helperId, chosenPositionId);
            }
            else
            {
                // inform user about the generation of additional mail ('PROPOSE_POSITIONS')
                // as the chosen position is already assigned to another helper
                return HtmlRenderer.renderChosenPositionUnavailableCallback(helperId, chosenPositionId);
            }
        }
        catch (Exception e)
        {
            // TODO really catch exception here (not MismatchingMessageCorrelationException ?!?)
            return HtmlRenderer.renderCorrelationFault(helperId);
        }
    }
    
    public static String processAssignmentCancellation(Long eventId, Long helperId)
    {
        // TODO use correct message here !!
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        try
        {
            BpmPlatform.getDefaultProcessEngine()
            .getRuntimeService()
            .correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey);
            return RETURN_CANCELLATION_NOMINAL;
        }
        catch (MismatchingMessageCorrelationException e)
        {
            return HtmlRenderer.renderCorrelationFault(helperId);
        }
    }
}