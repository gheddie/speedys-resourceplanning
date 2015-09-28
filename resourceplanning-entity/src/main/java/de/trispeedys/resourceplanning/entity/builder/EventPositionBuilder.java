package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.EventPosition;
import de.trispeedys.resourceplanning.entity.Position;

public class EventPositionBuilder extends AbstractEntityBuilder<EventPosition>
{
    private Position position;
    
    private EventOccurence eventOccurence;

    public EventPositionBuilder withEventOccurence(EventOccurence aEventOccurence)
    {
        eventOccurence = aEventOccurence;
        return this;
    }

    public EventPositionBuilder withPosition(Position aPosition)
    {
        position = aPosition;
        return this;
    }
    
    public EventPosition build()
    {
        EventPosition eventPosition = new EventPosition();
        eventPosition.setPosition(position);
        eventPosition.setEventOccurence(eventOccurence);
        return eventPosition;
    }
}