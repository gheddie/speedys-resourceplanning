package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckPreconditionsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // (1) check if business key is set
        if (execution.getBusinessKey() == null)
        {
            throw new ResourcePlanningException("can not start request help process without a business key!!");
        }

        // (2) check if helper id is set
        if (execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID) == null)
        {
            throw new ResourcePlanningException("can not start request help process without a helper id set!!");
        }

        // (3) check if event occurence id is set
        if (execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_OCCURENCE_ID) == null)
        {
            throw new ResourcePlanningException("can not start request help process without a event occurence id set!!");
        }
    }
}