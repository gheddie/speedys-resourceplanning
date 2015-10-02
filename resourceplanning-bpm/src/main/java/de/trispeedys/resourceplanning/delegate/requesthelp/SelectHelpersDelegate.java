package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class SelectHelpersDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_ACTIVE_HELPER_IDS, HelperService.queryActiveHelperIds());
    }
}