package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import java.util.Map;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.service.HelperService;

public class CheckAvailabilityOfPriorAssignmentDelegate extends CheckAvailabilityDelegate
{
    protected Position getReferencePosition(Helper helper, Map<String, Object> variables)
    {
        //check availability of the position which the helper was assigned to the last time
        return HelperService.getPriorAssignment(helper).getPosition();
    }
}