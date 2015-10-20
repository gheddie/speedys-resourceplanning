package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;

public abstract class RequestHelpDelegate implements JavaDelegate
{
    protected Helper getHelper(DelegateExecution execution)
    {
        Helper helper =
                Datasources.getDatasource(Helper.class).findById((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));
        return helper;
    }

    protected Event getEvent(DelegateExecution execution)
    {
        Event event =
                Datasources.getDatasource(Event.class).findById((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID));
        return event;
    }
}