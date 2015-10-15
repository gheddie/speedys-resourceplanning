package de.trispeedys.resourceplanning.delegate.requesthelp.availability;

import java.util.Map;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.variables.BpmVariables;

public class CheckAvailabilityOfChosenAssignmentDelegate extends CheckAvailabilityDelegate
{
    protected Position getReferencePosition(Helper helper, Map<String, Object> variables)
    {
        // check availability of the position which was just selected by the helper
        return (Position) DatasourceRegistry.getDatasource(Position.class).findById(Position.class,
                (Long) variables.get(BpmVariables.RequestHelpHelper.VAR_CHOSEN_POSITION));
    }
}