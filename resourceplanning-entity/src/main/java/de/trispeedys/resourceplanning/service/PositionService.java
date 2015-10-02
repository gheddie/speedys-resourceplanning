package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionService
{
    @SuppressWarnings("unchecked")
    public static List<Position> findPositionsInEvent(Event event)
    {
        List<Object[]> list = (List<Object[]>) DatasourceRegistry.getDatasource(null).find("FROM "+EventPosition.class.getSimpleName()+" ep INNER JOIN ep.position pos WHERE ep.event = :event", "event", event);
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
        List<Object[]> list = (List<Object[]>) DatasourceRegistry.getDatasource(null).find(queryString);
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

    /**
     * checks if the given {@link Position} is assigned to the {@link Event} by a {@link EventPosition} entry.
     * 
     * @param position
     * @param event
     * @return
     */
    public static boolean isPositionPresentInEvent(Position position, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("position", position);
        parameters.put("event", event);
        List<?> result = DatasourceRegistry.getDatasource(null).find("FROM " + EventPosition.class.getSimpleName() + " ep WHERE ep.position = :position AND ep.event = :event", parameters );
        return ((result != null) && (result.size() > 0));
    }
}