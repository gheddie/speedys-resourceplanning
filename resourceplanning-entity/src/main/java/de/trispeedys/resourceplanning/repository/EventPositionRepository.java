package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.EventPositionDatasource;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;

public class EventPositionRepository implements DatabaseRepository<EventPositionRepository>
{
    private EventPositionDatasource datasource;

    public void createDataSource()
    {
        datasource = new EventPositionDatasource();
    }

    public EventPosition findByEventAndPositionNumber(Event event, int positionNumber)
    {
        return (EventPosition) datasource.findSingle(EventPosition.ATTR_EVENT, event,
                EventPosition.ATTR_POSITION, RepositoryProvider.getRepository(PositionRepository.class)
                        .findPositionByPositionNumber(positionNumber));
    }
}