package de.trispeedys.resourceplanning.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionService
{
    @SuppressWarnings("unchecked")
    public static List<Position> findPositionsInEvent(Event event)
    {
        List<Object[]> list =
                (List<Object[]>) DatasourceRegistry.getDatasource(null).find(
                        "FROM " +
                                EventPosition.class.getSimpleName() +
                                " ep INNER JOIN ep.position pos WHERE ep.event = :event", "event", event);
        List<Position> result = new ArrayList<Position>();
        for (Object[] tuple : list)
        {
            result.add((Position) tuple[1]);
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public static boolean isPositionAvailable(Long eventId, Long positionId)
    {
        Position position = (Position) DatasourceRegistry.getDatasource(Position.class).findById(Position.class, positionId);
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, eventId);
        return isPositionAvailable(event, position);
    }

    /**
     * checks if the given position is present and already assigned in the given event
     * 
     * @param eventId
     * @param position
     * @return
     */
    @SuppressWarnings(
    {
        "unchecked"
    })
    public static boolean isPositionAvailable(Event event, Position position)
    {
        String queryString =
                "FROM " +
                        HelperAssignment.class.getSimpleName() +
                        " ec WHERE ec.position = :position AND ec.event = :event";
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put(HelperAssignment.ATTR_EVENT, event);
        variables.put(HelperAssignment.ATTR_POSITION, position);
        List<HelperAssignment> helperAssignments = DatasourceRegistry.getDatasource(HelperAssignment.class).find(queryString, variables);
        // no helper assignments -> position available
        return ((helperAssignments == null) || (helperAssignments.size() == 0));
    }

    /**
     * checks if the given {@link Position} is assigned to the {@link Event} by a {@link EventPosition} entry.
     * 
     * @param position
     * @param event
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isPositionPresentInEvent(Position position, Event event)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("position", position);
        parameters.put("event", event);
        List<?> result =
                DatasourceRegistry.getDatasource(EventPosition.class).find(
                        "FROM " +
                                EventPosition.class.getSimpleName() +
                                " ep WHERE ep.position = :position AND ep.event = :event", parameters);
        return ((result != null) && (result.size() > 0));
    }

    public static List<Position> findUnassignedPositionsInEvent(Event event)
    {
        // find positions for that event
        Session session = HibernateUtil.getSessionFactory().openSession();
        String qryString =
                "FROM " +
                        EventPosition.class.getSimpleName() + " ep WHERE ep." + EventPosition.ATTR_EVENT +
                        " = :event AND ep.position.id NOT IN (SELECT ec.position.id FROM " +
                        HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event)";
        Query q = session.createQuery(qryString);
        q.setParameter("event", event);
        List<EventPosition> eventPositions = q.list();
        session.close();
        List<Position> result = new ArrayList<Position>();
        for (EventPosition ep : eventPositions)
        {
            result.add(ep.getPosition());
        }
        return result;
    }
}