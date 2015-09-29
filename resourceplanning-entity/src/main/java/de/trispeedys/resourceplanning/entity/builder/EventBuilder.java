package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.trispeedys.resourceplanning.entity.Event;

public class EventBuilder extends AbstractEntityBuilder<Event>
{
    private String description;
    
    private Date eventDate;
    
    private String eventKey;

    public EventBuilder withDescription(String aDescription)
    {
        description = aDescription;
        return this;
    }
    
    public EventBuilder withDate(Date aEventDate)
    {
        eventDate = aEventDate;
        return this;
    }
    
    public EventBuilder withEventKey(String aEventKey)
    {
        eventKey = aEventKey;
        return this;
    }
    
    public Event build()
    {
        Event event = new Event();
        event.setDescription(description);
        event.setEventKey(eventKey);
        event.setEventDate(eventDate);
        return event;
    }
}