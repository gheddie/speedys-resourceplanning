package de.trispeedys.resourceplanning.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.DefaultDatasource;
import de.trispeedys.resourceplanning.datasource.PositionDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.HelperAssignment;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.repository.base.AbstractDatabaseRepository;
import de.trispeedys.resourceplanning.repository.base.DatabaseRepository;

public class PositionRepository extends AbstractDatabaseRepository<Position> implements DatabaseRepository<PositionRepository>
{
    public Position findPositionByPositionNumber(int positionNumber)
    {
        return (Position) dataSource().findSingle(Position.ATTR_POS_NUMBER, positionNumber);
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

    protected DefaultDatasource<Position> createDataSource()
    {
        return new PositionDatasource();
    }
}