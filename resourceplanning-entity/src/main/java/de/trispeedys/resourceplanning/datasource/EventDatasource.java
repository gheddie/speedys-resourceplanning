package de.trispeedys.resourceplanning.datasource;

import de.trispeedys.resourceplanning.entity.Event;

public class EventDatasource extends DefaultDatasource<Event>
{
    protected Class<Event> getType()
    {
        return Event.class;
    }
}