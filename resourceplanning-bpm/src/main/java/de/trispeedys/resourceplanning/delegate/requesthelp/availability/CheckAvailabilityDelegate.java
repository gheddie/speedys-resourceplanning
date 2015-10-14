package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public abstract class CheckAvailabilityDelegate implements JavaDelegate
{
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception
    {
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helperId);
        
        Position position = getReferencePosition(helper, execution.getVariables());
                              
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID));
        boolean positionAvailable = PositionService.isPositionAvailable(event , position);
        LoggerService.log(execution.getBusinessKey(), "ckecking availability for helper '"+helper+"' and position '"+position+"' in event '"+event+"', position available : " + positionAvailable);
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_POS_AVAILABLE_TO_REASSIGN, positionAvailable);
        // set id of the position
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, position.getId());
    }

    /**
     * determines the position of which the availability needs to be checked.
     * @param map 
     */
    protected abstract Position getReferencePosition(Helper helper, Map<String, Object> variables);
}