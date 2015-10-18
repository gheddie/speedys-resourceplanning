package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class ConfirmHelperDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // at this point, the variable indicating the chosen position must be set, so...
        if (execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION) == null)
        {
            throw new ResourcePlanningException("can not book helper to position for position id not set!!");
        }
        Position position = (Position) DatasourceRegistry.getDatasource(Position.class).findById(Position.class, (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION));
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID));
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID));
        AssignmentService.assignHelper(helper, event, position);
    }
}