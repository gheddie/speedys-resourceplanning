package de.trispeedys.resourceplanning.service;

import java.util.HashMap;
import java.util.List;

import org.joda.time.Days;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.util.DateHelper;
import de.trispeedys.resourceplanning.util.exception.ResourcePlanningException;

public class CommitmentService
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
        EntityFactory.buildEventCommitment(helper, event, position).persist();
    }

    /**
     * Ist der Helfer f�r irgendeine Position im gegebenen Event best�tigt?
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
        String queryString =
                "From " + EventCommitment.class.getSimpleName() + " ec WHERE ec.helper = :helper AND ec.event = :event";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("event", event);
        parameters.put("helper", helper);
        List<EventCommitment> list = (List<EventCommitment>) DatasourceRegistry.getDatasource(EventCommitment.class).find(queryString, parameters);
        return (list.size() > 0);
    }

    @SuppressWarnings("unchecked")
    public static List<EventCommitment> getAllCommitments(Long helperId)
    {
        String queryString = null;
        List<EventCommitment> list = null;
        queryString = "From " + EventCommitment.class.getSimpleName() + " ec WHERE ec.helperId = :helperId";
        list = (List<EventCommitment>) DatasourceRegistry.getDatasource(null).find(queryString, "helperId", helperId);
        return list;
    }

    /**
     * gets the {@link EventCommitment} for the given {@link Helper} in the given year (can be more
     * than one, as one helper can be assigned to multiple positions in one event).
     * 
     * @param helper
     * @param event
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<EventCommitment> getHelperCommitments(Helper helper, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventCommitment.ATTR_HELPER, helper);
        parameters.put(EventCommitment.ATTR_EVENT, event);
        List<EventCommitment> commitments = DatasourceRegistry.getDatasource(EventCommitment.class).find("FROM " + EventCommitment.class.getSimpleName() + " ec WHERE ec.event = :event AND ec.helper = :helper", parameters);
        return commitments;       
    }
}