package de.trispeedys.resourceplanning.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;

@Entity
@Table(name = "event")
public class Event extends AbstractDbObject
{
    @Temporal(TemporalType.DATE)
    @Column(name = "event_date")
    private Date eventDate;
    
    @Column(name = "event_key")
    @NotNull
    private String eventKey;
    
    @NotNull
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private List<EventPosition> eventPositions;

    @Column(name = "helpers_reminded")
    private boolean helpersReminded;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_state")
    @NotNull
    private EventState eventState;

    public Date getEventDate()
    {
        return eventDate;
    }
    
    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public String getEventKey()
    {
        return eventKey;
    }

    public void setEventKey(String eventKey)
    {
        this.eventKey = eventKey;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public List<EventPosition> getEventPositions()
    {
        return eventPositions;
    }
    
    public void setEventPositions(List<EventPosition> eventPositions)
    {
        this.eventPositions = eventPositions;
    }
    
    public boolean isHelpersReminded()
    {
        return this.helpersReminded;        
    }
    
    public void setHelpersReminded(boolean helpersReminded)
    {
        this.helpersReminded = helpersReminded;        
    }
    
    public EventState getEventState()
    {
        return eventState;
    }
    
    public void setEventState(EventState eventState)
    {
        this.eventState = eventState;
    }
    
    public String toString()
    {
        return getClass().getSimpleName() + " ["+description+", "+eventKey+"]";
    }
}