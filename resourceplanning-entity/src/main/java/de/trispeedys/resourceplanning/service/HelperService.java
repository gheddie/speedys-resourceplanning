package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.HelperCallback;
import de.trispeedys.resourceplanning.entity.misc.HelperState;

public class HelperService
{
    @SuppressWarnings("unchecked")
    public static EventCommitment getLastConfirmedAssignmentForHelper(Long helperId)
    {
        String queryString =
                "From " +
                        EventCommitment.class.getSimpleName() +
                        " ec INNER JOIN ec.event eo WHERE ec.helperId = :helperId ORDER BY eo.eventDate DESC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helperId", helperId);
        List<Object[]> list = (List<Object[]>) DatasourceRegistry.getDatasource(null).find(queryString, parameters);
        if (list.size() == 0)
        {
            return null;
        }
        return (EventCommitment) list.get(0)[0];
    }

    /**
     * Finds the last confirmed assignment for the given helper an checks whether the helper can be reassigned in the
     * same position in the given event (if the positions still exists and if it is not not already occupied).
     * 
     * @param event
     * @param helper
     */
    public static boolean isHelperReassignableToSamePosition(Long eventId, Long helperId)
    {
        // is the position of the last assignment available for this event?
        return PositionService.isPositionAssigned(eventId, getLastConfirmedAssignmentForHelper(helperId).getPosition());
    }

    public static boolean isFirstAssignment(Long helperId)
    {
        List<EventCommitment> commitments = CommitmentService.getAllCommitments(helperId);
        return ((commitments == null) || (commitments.size() == 0));
    }

    @SuppressWarnings("unchecked")
    public static List<Long> queryActiveHelperIds()
    {
        List<Long> result = new ArrayList<Long>();
        for (Object helper : DatasourceRegistry.getDatasource(Helper.class).find(Helper.class, "helperState",
                HelperState.ACTIVE))
        {
            result.add(((AbstractDbObject) helper).getId());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void deactivateHelper(Long helperId)
    {
        DefaultDatasource<?> datasource = DatasourceRegistry.getDatasource(Helper.class);
        Helper helper = (Helper) datasource.findById(Helper.class, helperId);
        helper.setHelperState(HelperState.INACTIVE);
        datasource.saveOrUpdate(helper);
    }

    @SuppressWarnings("unchecked")
    public static boolean isHelperAssignedForPosition(Helper helper, Event event, Position position)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helper", helper);
        parameters.put("event", event);
        parameters.put("position", position);
        List<EventCommitment> list = DatasourceRegistry.getDatasource(Helper.class).find(
                "FROM " +
                        EventCommitment.class.getSimpleName() +
                        " ec WHERE ec.helper = :helper AND ec.event = :event AND ec.position = :position", parameters);
        return ((list != null) || (list.size() == 1));
    }

    @SuppressWarnings("unchecked")
    public static Position getHelperAssignment(Helper helper, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helper", helper);
        parameters.put("event", event);
        List<EventCommitment> commitments =
                DatasourceRegistry.getDatasource(EventCommitment.class).find(
                        "FROM " +
                                EventCommitment.class.getSimpleName() +
                                " ec WHERE ec.event = :event AND ec.helper = :helper", parameters);
        if ((commitments == null) || (commitments.size() == 0))
        {
            return null;
        }
        return commitments.get(0).getPosition();
    }
}