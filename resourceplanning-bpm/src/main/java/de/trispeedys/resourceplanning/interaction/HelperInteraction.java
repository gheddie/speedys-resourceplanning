package de.trispeedys.resourceplanning.interaction;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
    public static void processReminderCallback(HttpServletRequest request)
    {
        Long helperId = Long.parseLong(request.getParameter("helperId"));
        Long eventId = Long.parseLong(request.getParameter("eventId"));
        HelperCallback callback = HelperCallback.valueOf(request.getParameter("callbackResult"));
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        processReminderCallback(callback, businessKey);
    }

    public static void processReminderCallback(HelperCallback callback, String businessKey)
    {
        Map<String, Object> variables = new HashMap<String, Object>();
        switch (callback)
        {
            case ASSIGNMENT_AS_BEFORE:
                LoggerService.log("the helper wants to be assigned as before...", DbLogLevel.INFO);
                variables.put(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK, HelperCallback.ASSIGNMENT_AS_BEFORE);
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
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_HELP_CALLBACK, businessKey, variables);
    }

    public static void processPositionChosenCallback(HttpServletRequest request, boolean positionAvailable) throws MismatchingMessageCorrelationException
    {
        Long chosenPositionId = Long.parseLong(request.getParameter("chosenPosition"));
        Long helperId = Long.parseLong(request.getParameter("helperId"));
        Long eventId = Long.parseLong(request.getParameter("eventId"));
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, chosenPositionId);
        variables.put(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, positionAvailable);
        String businessKey = ResourcePlanningUtil.generateRequestHelpBusinessKey(helperId, eventId);
        BpmPlatform.getDefaultProcessEngine().getRuntimeService().correlateMessage(BpmMessages.RequestHelpHelper.MSG_POS_CHOSEN, businessKey, variables);
    }
}