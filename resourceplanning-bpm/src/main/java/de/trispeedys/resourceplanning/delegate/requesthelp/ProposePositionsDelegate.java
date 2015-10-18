package de.trispeedys.resourceplanning.delegate.requesthelp;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.ProposePositionsMailTemplate;
import de.trispeedys.resourceplanning.service.HelperService;
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class ProposePositionsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        // send a mail with all unassigned positions in the current event
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, eventId);
        List<Position> unassignedPositions = PositionService.findUnassignedPositionsInEvent(event);
        if ((unassignedPositions == null) || (unassignedPositions.size() == 0))
        {
            throw new ResourcePlanningException("can not propose any unassigned positions as there are none!!");
        }
        // send mail
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) DatasourceRegistry.getDatasource(Helper.class).findById(Helper.class, helperId);
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(helper, event, unassignedPositions,
                        (HelperCallback) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK),
                        HelperService.getPriorAssignment(helper, event.getEventTemplate()).getPosition());
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.getSubject(),
                template.getBody(), MessagingType.PROPOSE_POSITIONS, MessagingFormat.HTML).persist();
    }
}