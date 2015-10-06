package de.trispeedys.resourceplanning.delegate.requesthelp;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckAvailabiliyDelegate implements JavaDelegate
{
    private static final Logger logger = Logger.getLogger(CheckAvailabiliyDelegate.class);

    public void execute(DelegateExecution execution) throws Exception
    {
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        boolean positionAssigned = PositionService.isPositionAssigned(eventId, HelperService.getLastConfirmedAssignmentForHelper(helperId)
                .getPosition());
        execution.setVariable(BpmVariables.RequestHelpHelper.VAR_POS_AVAILABLE_TO_REASSIGN,
                positionAssigned);
    }
}