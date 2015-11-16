package de.trispeedys.resourceplanning.delegate.requesthelp;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import de.trispeedys.resourceplanning.delegate.requesthelp.misc.RequestHelpNotificationDelegate;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.execution.BpmVariables;
import de.trispeedys.resourceplanning.messaging.ProposePositionsMailTemplate;
import de.trispeedys.resourceplanning.rule.ChoosablePositionGenerator;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class ProposePositionsDelegate extends RequestHelpNotificationDelegate
{
    public void execute(DelegateExecution execution) throws Exception
    {
        //PositionRepository positionRepository = RepositoryProvider.getRepository(PositionRepository.class);
        // send a mail with all unassigned positions in the current event
        Event event = getEvent(execution);
        //List<Position> unassignedPositions = positionRepository.findUnassignedPositionsInEvent(event, true);
        List<Position> unassignedPositions = new ChoosablePositionGenerator().generate(null, event);
        if ((unassignedPositions == null) || (unassignedPositions.size() == 0))
        {
            throw new ResourcePlanningException(
                    "can not propose any unassigned positions as there are none!!");
        }
        // send mail
        Helper helper = getHelper(execution);
        boolean isReentrant = false;
        Object varReentrant = execution.getVariable(BpmVariables.RequestHelpHelper.VAR_POS_CHOOSING_REENTRANT);
        if (varReentrant != null)
        {
            if ((Boolean) varReentrant)
            {
                isReentrant = true;
            }
        }
        ProposePositionsMailTemplate template =
                new ProposePositionsMailTemplate(
                        helper,
                        event,
                        unassignedPositions,
                        (HelperCallback) execution.getVariable(BpmVariables.RequestHelpHelper.VAR_HELPER_CALLBACK),
                        AssignmentService.getPriorAssignment(helper, event.getEventTemplate()).getPosition(), isReentrant);
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(), template.constructSubject(),
                template.constructBody(), template.getMessagingType(), template.getMessagingFormat()).saveOrUpdate();
    }
}