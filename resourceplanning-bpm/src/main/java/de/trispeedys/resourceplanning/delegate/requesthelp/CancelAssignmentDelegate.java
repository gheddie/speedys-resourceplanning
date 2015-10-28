package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.service.AssignmentService;

public class CancelAssignmentDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Event event = getEvent(execution);
        Helper helper = getHelper(execution);
        AssignmentService.cancelHelperAssignment(helper, event);
    }
}