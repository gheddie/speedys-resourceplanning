package de.trispeedys.resourceplanning.delegate.speedystestprocess;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SomeTestDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        System.out.println(" --- SomeTestDelegate --- ");
    }
}