package de.trispeedys.resourceplanning.test;

import java.util.List;

import de.trispeedys.resourceplanning.entity.DatasourceRegistry;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;

public class DatabaseRoutines
{
    @SuppressWarnings("unchecked")
    public static Event duplicateEvent(Long eventId, String description, String eventKey, int day, int month, int year)
    {
        Event event = (Event) DatasourceRegistry.getDatasource(Event.class).findById(Event.class, eventId);
        if (event == null)
        {
            return null;
        }
        Event newEvent = EntityFactory.buildEvent(description, eventKey, day, month, year).persist();
        List<EventPosition> positions = (List<EventPosition>) DatasourceRegistry.getDatasource(null).find(EventPosition.class, "event", event);
        System.out.println(positions.size());
        Position oldPos = null;
        Position newPos = null;
        for (EventPosition evtpos : positions)
        {            
            oldPos = evtpos.getPosition();
            //duplicate position...
            newPos = EntityFactory.buildPosition(oldPos.getDescription(), oldPos.getMinimalAge(), oldPos.getDomain()).persist();
            //..and attach it to the new event
            EntityFactory.buildEventPosition(newEvent, newPos).persist();
        }
        return newEvent;
    }
}