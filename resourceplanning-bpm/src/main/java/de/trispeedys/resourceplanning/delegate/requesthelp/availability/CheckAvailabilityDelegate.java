package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.LoggerService;
import de.trispeedys.resourceplanning.service.PositionService;

public class CheckAvailabilityDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        Helper helper = getHelper(execution);
        Event event = getEvent(execution);
        Position position = AssignmentService.getPriorAssignment(helper, event.getEventTemplate()).getPosition();
        boolean positionAvailable = PositionService.isPositionAvailable(event, position);
        LoggerService.log(execution.getBusinessKey(), "ckecking availability for helper '" +
                helper + "' and position '" + position + "' in event '" + event + "', position available : " +
                positionAvailable);
        
        // set id of the position and the information, if the prior position is avaiablable...
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_PRIOR_POS_AVAILABLE, positionAvailable);
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION, position.getId());
    }
}