package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.exception.ResourcePlanningException;

public class CheckPreconditionsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        if (execution.getBusinessKey() == null)
        {
            throw new ResourcePlanningException("can not start request help process without a business key!!");
        }
    }
}