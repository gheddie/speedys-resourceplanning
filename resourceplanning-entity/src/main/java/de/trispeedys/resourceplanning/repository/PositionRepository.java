package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.PositionDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionRepository implements DatabaseRepository<PositionRepository>
{
    private PositionDatasource datasource;

    public Position findPositionByPositionNumber(int positionNumber)
    {
        return (Position) datasource.findSingle(Position.ATTR_POS_NUMBER, positionNumber);
    }

    public List<Position> findUnassignedPositionsInEvent(Event event)
    {
        String qryString =
                "FROM " +
                        EventPosition.class.getSimpleName() + " ep WHERE ep." + EventPosition.ATTR_EVENT +
                        " = :event AND ep.position.id NOT IN (SELECT ec.position.id FROM " +
                        HelperAssignment.class.getSimpleName() + " ec WHERE ec.event = :event)";
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("event", event);
        List<EventPosition> eventPositions =
                Datasources.getDatasource(EventPosition.class).find(qryString, parameters);
        List<Position> result = new ArrayList<Position>();
        for (EventPosition ep : eventPositions)
        {
            result.add(ep.getPosition());
        }
        return result;
    }

    public List<Position> findPositionsInEvent(Event event)
    {
        List<EventPosition> list =
                Datasources.getDatasource(EventPosition.class).find(
                        "FROM " +
                                EventPosition.class.getSimpleName() +
                                " ep INNER JOIN ep.position pos WHERE ep.event = :event", "event", event);
        List<Position> result = new ArrayList<Position>();
        Object[] tuple = null;
        for (Object obj : list)
        {
            tuple = (Object[]) obj;
            result.add((Position) tuple[1]);
        }
        return result;
    }

    public void createDataSource()
    {
        datasource = new PositionDatasource();
    }
}