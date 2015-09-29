package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckAvailabiliyDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_POS_AVAILABLE_TO_REASSIGN,
                HelperService.isHelperReassignableToSamePosition(
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID),
                        (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID)));
    }
}