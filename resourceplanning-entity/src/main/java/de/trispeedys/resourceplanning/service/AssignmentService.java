package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import org.joda.time.Days;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.DateHelper;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class AssignmentService
{
    public static void confirmHelper(Helper helper, Event event, Position position) throws ResourcePlanningException
    {
        /*
        if (isHelperConfirmedForEvent(event, helper))
        {
            throw new ResourcePlanningException("helper is already confirmed for another position!");
        }
        */
        if (!(position.isAuthorityOverride()))
        {
            //no authority override -> check age
            int dayDiff =
                    Days.daysBetween(DateHelper.toDateTime(event.getEventDate()),
                            DateHelper.toDateTime(helper.getDateOfBirth()).plusYears(position.getMinimalAge())).getDays();
            if (dayDiff > 0)
            {
                throw new ResourcePlanningException("helper is " + dayDiff + " days to young for this position!");
            }
        }
        EntityFactory.buildHelperAssignment(helper, event, position).persist();
    }

    @SuppressWarnings("unchecked")
    public static List<HelperAssignment> getAllHelperAssignments(Long helperId)
    {
        String queryString = null;
        List<HelperAssignment> list = null;
        queryString = "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId";
        list = (List<HelperAssignment>) DatasourceRegistry.getDatasource(null).find(queryString, "helperId", helperId);
        return list;
    }

    /**
     * gets the {@link HelperAssignment} for the given {@link Helper} in the given year (can be more
     * than one, as one helper can be assigned to multiple positions in one event).
     * 
     * @param helper
     * @param event
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<HelperAssignment> getHelperAssignments(Helper helper, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(HelperAssignment.ATTR_HELPER, helper);
        parameters.put(HelperAssignment.ATTR_EVENT, event);
        List<HelperAssignment> assignments = DatasourceRegistry.getDatasource(HelperAssignment.class).find("FROM " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event AND ec.helper = :helper", parameters);
        return assignments;       
    }
}