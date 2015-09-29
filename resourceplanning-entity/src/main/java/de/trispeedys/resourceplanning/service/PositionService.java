package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionService
{
    @SuppressWarnings("unchecked")
    public static List<Position> findPositionsInEvent(Event event)
    {
        List<Object[]> list = (List<Object[]>) HibernateUtil.fetchResults("FROM "+EventPosition.class.getSimpleName()+" ep INNER JOIN ep.position pos WHERE ep.event = :event", "event", event);
        List<Position> result = new ArrayList<Position>();
        for (Object[] tuple : list)
        {
            result.add((Position) tuple[1]);
        }
        return result;
    }

    /**
     * checks if the given position is present and already assigned in the given event
     * 
     * @param eventId
     * @param position
     * @return
     */
    @SuppressWarnings({
            "unchecked"
    })
    public static boolean isPositionAssigned(Long eventId, Position position)
    {
        if (!(isPositionScheduledForEvent(eventId, position)))
        {
            return false;
        }
        String queryString = "From " +
                EventCommitment.class.getSimpleName() + " ec WHERE ec.position = :position";
        List<Object[]> list = (List<Object[]>) HibernateUtil.fetchResults(queryString);
        return false;
    }

    /**
     * Checks if the given position is present in the given event.
     * 
     * @param eventId
     * @param position
     * @return
     */
    public static boolean isPositionScheduledForEvent(Long eventId, Position position)
    {
        return false;
    }
}