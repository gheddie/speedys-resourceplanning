package de.trispeedys.resourceplanning.service;

import java.util.List;

import org.joda.time.Days;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.DateHelper;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class AssignmentService
{
    public static void assignHelper(Helper helper, Event event, Position position)
            throws ResourcePlanningException
    {
        if (!(position.isAuthorityOverride()))
        {
            // no authority override -> check age
            int dayDiff =
                    Days.daysBetween(
                            DateHelper.toDateTime(event.getEventDate()),
                            DateHelper.toDateTime(helper.getDateOfBirth())
                                    .plusYears(position.getMinimalAge())).getDays();
            if (dayDiff > 0)
            {
                throw new ResourcePlanningException("helper is " +
                        dayDiff + " days to young for this position!");
            }
        }
        EntityFactory.buildHelperAssignment(helper, event, position).persist();
    }

    public static List<HelperAssignment> getAllHelperAssignments(Long helperId)
    {
        return DatasourceRegistry.getDatasource(HelperAssignment.class).find(
                "From " + HelperAssignment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId",
                "helperId", helperId);
    }

    /**
     * gets the {@link HelperAssignment} for the given {@link Helper} in the given year (can be more than one, as one
     * helper can be assigned to multiple positions in one event).
     * 
     * @param helper
     * @param event
     * @return
     */
    public static List<HelperAssignment> getHelperAssignments(Helper helper, Event event)
    {
        return DatasourceRegistry.getDatasource(HelperAssignment.class).find(HelperAssignment.class,
                HelperAssignment.ATTR_HELPER, helper, HelperAssignment.ATTR_EVENT, event);
    }

    public static boolean isFirstAssignment(Long helperId)
    {
        List<HelperAssignment> helperAssignments = AssignmentService.getAllHelperAssignments(helperId);
        return ((helperAssignments == null) || (helperAssignments.size() == 0));
    }

    /**
     * Sets a {@link HelperAssignment} to state {@link HelperAssignmentState#CANCELLED}.
     */
    public static void cancelHelperAssignment(Helper helper, Event event)
    {
        DefaultDatasource<HelperAssignment> datasource = DatasourceRegistry.getDatasource(HelperAssignment.class);
        HelperAssignment assignment =
                datasource
                        .find(HelperAssignment.class, HelperAssignment.ATTR_HELPER, helper,
                                HelperAssignment.ATTR_EVENT, event)
                        .get(0);
        assignment.setHelperAssignmentState(HelperAssignmentState.CANCELLED);
        datasource.saveOrUpdate(assignment);
    }
}