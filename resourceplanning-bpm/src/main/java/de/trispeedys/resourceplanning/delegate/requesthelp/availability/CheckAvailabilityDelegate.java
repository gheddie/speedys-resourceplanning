package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.PositionService;

public class CheckAvailabilityDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper =
                (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(helperId);
        Event event =
                (Event) DatasourceRegistry.getDatasource(Event.class).findById((Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID));
        Position position = AssignmentService.getPriorAssignment(helper, event.getEventTemplate()).getPosition();
        boolean positionAvailable = PositionService.isPositionAvailable(event, position);
        LoggerService.log(execution.getBusinessKey(), "ckecking availability for helper '" +
                helper + "' and position '" + position + "' in event '" + event + "', position available : " +
                positionAvailable);
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE, positionAvailable);
        // set id of the position
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, position.getId());
    }
}