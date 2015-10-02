package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckPriorAssignmentsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        boolean firstAssignment = HelperService.isFirstAssignment((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));
        execution.setVariable(
                BpmVariables.RequestHelpHelper.VAR_FIRST_ASSIGNMENT,
                firstAssignment);
        //set mails attempts to 0
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_MAIL_ATTEMPTS, 0);
    }
}