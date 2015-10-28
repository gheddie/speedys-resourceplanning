package de.trispeedys.resourceplanning.delegate.requesthelp;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.MessagingType;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.MessagingFormat;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.ProposePositionsMailTemplate;
import de.trispeedys.resourceplanning.repository.PositionRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class ProposePositionsDelegate extends RequestHelpDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);
        // send a mail with all unassigned positions in the current event
        Event event = getEvent(execution);
        List<Position> unassignedPositions = positionRepository.findUnassignedPositionsInEvent(event);
        if ((unassignedPositions == null) || (unassignedPositions.size() == 0))
        {
            throw new ResourcePlanningException(
                    "can not propose any unassigned positions as there are none!!");
        }
        // send mail
        Helper helper = getHelper(execution);
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(
                        helper,
                        event,
                        unassignedPositions,
                        (HelperCallback) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK),
                        AssignmentService.getPriorAssignment(helper, event.getEventTemplate()).getPosition());
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.getSubject(),
                template.getBody(), MessagingType.PROPOSE_POSITIONS, MessagingFormat.HTML).persist();
    }
}