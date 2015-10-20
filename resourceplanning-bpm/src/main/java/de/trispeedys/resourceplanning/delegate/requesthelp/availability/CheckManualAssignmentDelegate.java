package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class CheckManualAssignmentDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        if (execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION) == null)
        {
            throw new ResourcePlanningException("chosen position id must not be null at this point!!");
        }
        if (PositionService.isPositionAvailable(getEvent(execution).getId(), (Long) execution.getVariableLocal(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION)))
        {
            execution.setVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POS_AVAILABLE, true);
        }
        else
        {
            throw new ResourcePlanningException("chosen position is not available!!");
        }
    }
}