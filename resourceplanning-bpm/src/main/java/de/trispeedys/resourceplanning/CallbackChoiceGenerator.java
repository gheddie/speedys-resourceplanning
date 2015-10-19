package de.trispeedys.resourceplanning;

import java.util.Arrays;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.service.AssignmentService;
import de.trispeedys.resourceplanning.service.PositionService;

public class CallbackChoiceGenerator
{
    public List<HelperCallback> generateChoices(Helper helper, Event event)
    {
        if (AssignmentService.isFirstAssignment(helper.getId()))
        {
            return null;
        }
        Position priorPosition =
                AssignmentService.getPriorAssignment(helper, event.getEventTemplate()).getPosition();
        if (!(PositionService.isPositionAvailable(event, priorPosition)))
        {
            // prior position is not available, so...
            return Arrays.asList(new HelperCallback[] {HelperCallback.CHANGE_POS, HelperCallback.PAUSE_ME});
        }
        return Arrays.asList(HelperCallback.values());
    }
}