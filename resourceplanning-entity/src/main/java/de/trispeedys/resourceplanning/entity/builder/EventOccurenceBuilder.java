package de.trispeedys.resourceplanning.entity.builder;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.trispeedys.resourceplanning.entity.EventOccurence;

public class EventOccurenceBuilder extends AbstractEntityBuilder<EventOccurence>
{
    private String description;
    
    private Date eventDate;
    
    private String eventKey;

    public EventOccurenceBuilder withDescription(String aDescription)
    {
        description = aDescription;
        return this;
    }
    
    public EventOccurenceBuilder withDate(Date aEventDate)
    {
        eventDate = aEventDate;
        return this;
    }
    
    public EventOccurenceBuilder withEventKey(String aEventKey)
    {
        eventKey = aEventKey;
        return this;
    }
    
    public EventOccurence build()
    {
        EventOccurence eventOccurence = new EventOccurence();
        eventOccurence.setDescription(description);
        eventOccurence.setEventKey(eventKey);
        eventOccurence.setEventDate(eventDate);
        return eventOccurence;
    }
}