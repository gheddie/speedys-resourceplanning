package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.Datasources;
import de.trispeedys.resourceplanning.datasource.EventPositionDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class EventPositionRepository implements DatabaseRepository<EventPositionRepository>
{
    private EventPositionDatasource datasource;

    public void createDataSource()
    {
        datasource = new EventPositionDatasource();
    }

    public EventPosition findByEventAndPositionNumber(Event event, int positionNumber)
    {
        Position findSingle = Datasources.getDatasource(Position.class).findSingle(Position.ATTR_POS_NUMBER, positionNumber);
        return (EventPosition) datasource.findSingle(EventPosition.ATTR_EVENT, event,
                EventPosition.ATTR_POSITION, findSingle);
    }
}