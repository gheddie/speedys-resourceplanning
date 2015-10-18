package de.trispeedys.resourceplanning;

import java.util.Arrays;
import java.util.List;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.service.AssignmentService;

public class CallbackChoiceGenerator
{
    public List<HelperCallback> generateChoices(Helper helper)
    {
        if (AssignmentService.isFirstAssignment(helper.getId()))
        {
            return null;
        }
        
        // TODO not done yet...
        
        return Arrays.asList(HelperCallback.values());
    }
}