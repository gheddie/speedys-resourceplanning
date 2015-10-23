package de.trispeedys.resourceplanning.delegate.requesthelp;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import de.trispeedys.resourceplanning.datasource.Datasources;
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
import de.trispeedys.resourceplanning.repository.RepositoryProvider;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class ProposePositionsDelegate implements JavaDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);
        
        // send a mail with all unassigned positions in the current event
        Long eventId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_EVENT_ID);
        Event event = (Event) Datasources.getDatasource(Event.class).findById(eventId);
        List<Position> unassignedPositions = positionRepository.findUnassignedPositionsInEvent(event);
        if ((unassignedPositions == null) || (unassignedPositions.size() == 0))
        {
            throw new ResourcePlanningException("can not propose any unassigned positions as there are none!!");
        }
        // send mail
        Long helperId = (Long) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_ID);
        Helper helper = (Helper) Datasources.getDatasource(Helper.class).findById(helperId);
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(helper, event, unassignedPositions,
                        (HelperCallback) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK),
                        AssignmentService.getPriorAssignment(helper, event.getEventTemplate()).getPosition());
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.getSubject(),
                template.getBody(), MessagingType.PROPOSE_POSITIONS, MessagingFormat.HTML).persist();
    }
}