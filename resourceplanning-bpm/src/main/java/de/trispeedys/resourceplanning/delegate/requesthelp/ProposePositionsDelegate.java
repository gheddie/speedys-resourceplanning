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
import de.trispeedys.resourceplanning.service.PositionService;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class ProposePositionsDelegate implements JavaDelegate
{
    @SuppressWarnings("unchecked")
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
        EntityFactory.buildMessageQueue("noreply@tri-speedys.de", helper.getEmail(),
                "Positions-Auswahl für den Wettkampf " + event.getDescription(), generateProposalBody(helper, event, unassignedPositions), MessagingType.PROPOSE_POSITIONS, MessagingFormat.PLAIN).persist();
    }

    private String generateProposalBody(Helper helper, Event event, List<Position> unassignedPositions)
    {
        //build message body
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hallo, " + helper.getFirstName() + "!!");
        buffer.append("\n\n");
        buffer.append("Bitte sag uns, welche Position du beim "+event.getDescription()+" besetzen möchtest:");
        buffer.append("\n\n");
        for (Position unassignedPosition : unassignedPositions)
        {
            buffer.append("http://localhost:8080/resourceplanning-bpm-0.0.1-SNAPSHOT/ChosenPositionReceiver.jsp?chosenPosition=" +
                    unassignedPosition.getId() + "&helperId=" + helper.getId() + "&eventId=" + event.getId());
            buffer.append("\n\n");
        }
        buffer.append("Eure Speedys");
        return buffer.toString();
    }
}