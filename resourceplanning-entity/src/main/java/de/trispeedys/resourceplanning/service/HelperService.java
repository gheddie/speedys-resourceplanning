package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.misc.EventCommitmentState;

public class HelperService
{
    @SuppressWarnings("unchecked")
    public static EventCommitment getLastConfirmedAssignmentForHelper(Long helperId)
    {
        String queryString = "From " +
                EventCommitment.class.getSimpleName() +
                " ec INNER JOIN ec.event eo WHERE ec.helperId = :helperId AND ec.commitmentState = '" +
                EventCommitmentState.CONFIRMED + "' ORDER BY eo.eventDate DESC";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("helperId", helperId);
        List<Object[]> list = (List<Object[]>) HibernateUtil.fetchResults(queryString, parameters );
        if (list.size() == 0)
        {
            return null;
        }
        return (EventCommitment) list.get(0)[0];
    }

    /**
     * Finds the last confirmed assignment for the given helper an checks whether the helper can be reassigned in the same
     * position in the given event (if the positions still exists and if it is not not already occupied).
     * 
     * @param event
     * @param helper
     */
    public static boolean isHelperReassignableToSamePosition(Long eventId, Long helperId)
    {
        //is the position of the last assignment available for this event? 
        return PositionService.isPositionAssigned(eventId, getLastConfirmedAssignmentForHelper(helperId).getPosition());
    }

    public static boolean isFirstAssignment(Long helperId)
    {
        List<EventCommitment> commitments = CommitmentService.getAllCommitmentsByState(helperId, EventCommitmentState.CONFIRMED);
        return ((commitments == null) || (commitments.size() == 0));
    }
}