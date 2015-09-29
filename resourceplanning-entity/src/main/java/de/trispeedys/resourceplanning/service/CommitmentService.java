package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import org.joda.time.Days;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.exception.ResourcePlanningException;
import de.trispeedys.resourceplanning.util.DateHelper;

public class CommitmentService
{
    public static void confirmHelper(Helper helper, Event event, Position position) throws ResourcePlanningException
    {
        if (isHelperConfirmedForEvent(event, helper))
        {
            throw new ResourcePlanningException("helper is already confirmed for another position!");
        }
        int dayDiff =
                Days.daysBetween(DateHelper.toDateTime(event.getEventDate()),
                        DateHelper.toDateTime(helper.getDateOfBirth()).plusYears(position.getMinimalAge())).getDays();
        if (dayDiff > 0)
        {
            throw new ResourcePlanningException("helper is " + dayDiff + " days to young for this position!");
        }
        EntityFactory.buildEventCommitment(helper, event, position, EventCommitmentState.CONFIRMED).persist();
    }

    /**
     * Ist der Helfer für irgendeine Position im gegebenen Event bestätigt?
     * 
     * @param event
     * @param helper
     * @return
     * @throws ResourcePlanningException
     */
    @SuppressWarnings("unchecked")
    public static boolean isHelperConfirmedForEvent(Event event, Helper helper) throws ResourcePlanningException
    {
        // get confirmed positions for this helper in this event
        String queryString = "From " +
                EventCommitment.class.getSimpleName() +
                " ec WHERE ec.helper = :helper AND ec.event = :event AND ec.commitmentState = '" +
                EventCommitmentState.CONFIRMED + "'";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("event", event);
        parameters.put("helper", helper);
        List<EventCommitment> list = (List<EventCommitment>) HibernateUtil.fetchResults(queryString, parameters);
        return (list.size() > 0);
    }

    @SuppressWarnings("unchecked")
    public static List<EventCommitment> getAllCommitmentsByState(Long helperId,
            EventCommitmentState eventCommitmentState)
    {
        String queryString = null;
        List<EventCommitment> list = null;
        if (eventCommitmentState != null)
        {
            // query for commitments with a state
            queryString =
                    "From " +
                            EventCommitment.class.getSimpleName() +
                            " ec WHERE ec.helperId = :helperId AND ec.commitmentState = :commitmentState AND ec.commitmentState = '" +
                            eventCommitmentState + "'";
            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("commitmentState", eventCommitmentState);
            parameters.put("helperId", helperId);
            list = (List<EventCommitment>) HibernateUtil.fetchResults(queryString, parameters);
        }
        else
        {
            // query for commitments without a state (by helper only)
            queryString = "From " + EventCommitment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId";
            list = (List<EventCommitment>) HibernateUtil.fetchResults(queryString, "helperId", helperId);
        }
        return list;
    }
}