package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import org.joda.time.Days;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperAssignmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.HelperAssignmentRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
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
        EntityFactory.buildHelperAssignment(helper, event, position).saveOrUpdate();
    }

    public static boolean isFirstAssignment(Long helperId)
    {
        List<HelperAssignment> helperAssignments = RepositoryProvider.getRepository(HelperAssignmentRepository.class).getAllHelperAssignments(helperId);
        return ((helperAssignments == null) || (helperAssignments.size() == 0));
    }

    /**
     * Sets a {@link HelperAssignment} to state {@link HelperAssignmentState#CANCELLED}.
     */
    public static void cancelHelperAssignment(Helper helper, Event event)
    {
        HelperAssignmentRepository repository = RepositoryProvider.getRepository(HelperAssignmentRepository.class);
        HelperAssignment assignment = repository.findByHelperAndEvent(helper, event).get(0);
        assignment.setHelperAssignmentState(HelperAssignmentState.CANCELLED);
        repository.saveOrUpdate(assignment);
    }

    public static HelperAssignment getPriorAssignment(Helper helper, EventTemplate eventTemplate)
    {
        String queryString =
                "From " +
                        HelperAssignment.class.getSimpleName() +
                        " ha INNER JOIN ha.event ev WHERE ha.helperId = :helperId AND ev.eventTemplate = :eventTemplate ORDER BY ev.eventDate DESC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helperId", helper.getId());
        parameters.put("eventTemplate", eventTemplate);
        List<Object[]> list = Datasources.getDatasource(HelperAssignment.class).find(queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        return (HelperAssignment) list.get(0)[0];
    }
}