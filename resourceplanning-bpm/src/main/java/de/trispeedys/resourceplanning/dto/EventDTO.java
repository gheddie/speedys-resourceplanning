package de.trispeedys.resourceplanning.dto;

import de.trispeedys.resourceplanning.entity.misc.annotation.Display;

public class EventDTO
{
    @Display
    private String description;
    
    private Long eventId;
    
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;        
    }
}