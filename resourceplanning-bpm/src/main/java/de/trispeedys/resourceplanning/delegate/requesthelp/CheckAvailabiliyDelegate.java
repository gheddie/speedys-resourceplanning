package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckAvailabiliyDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        EventCommitment commitment =
                HelperService.getLastConfirmedAssignmentForHelper((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));
    }
}