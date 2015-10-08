package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckAvailabiliyDelegate implements JavaDelegate
{
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception
    {
        LoggerService.log(execution.getBusinessKey(), "ckecking availability...");
        
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Position position = HelperService.getLastConfirmedAssignmentForHelper(helperId).getPosition();
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID));
        boolean positionAvailable = PositionService.isPositionAvailable(event , position);
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_POS_AVAILABLE_TO_REASSIGN, positionAvailable);
        // set id of the position
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, position.getId());
    }
}