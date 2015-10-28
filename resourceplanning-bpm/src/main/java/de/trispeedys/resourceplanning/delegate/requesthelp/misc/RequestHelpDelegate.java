package de.trispeedys.resourceplanning.delegate.requesthelp.misc;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.repository.EventRepository;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;

public abstract class RequestHelpDelegate implements JavaDelegate
{
    protected Helper getHelper(DelegateExecution execution)
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = RepositoryProvider.getRepository(HelperRepository.class).findById(helperId);
        return helper;
    }

    protected Event getEvent(DelegateExecution execution)
    {
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        Event event = RepositoryProvider.getRepository(EventRepository.class).findById(eventId);
        return event;
    }
}