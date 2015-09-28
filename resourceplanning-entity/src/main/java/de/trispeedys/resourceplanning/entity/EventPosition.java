package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "event_position")
public class EventPosition extends AbstractDbObject
{
    @OneToOne
    @JoinColumn(name = "position_id")
    private Position position;
    
    @OneToOne
    @JoinColumn(name = "event_occurence_id")
    private EventOccurence eventOccurence;

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }

    public EventOccurence getEventOccurence()
    {
        return eventOccurence;
    }

    public void setEventOccurence(EventOccurence eventOccurence)
    {
        this.eventOccurence = eventOccurence;
    } 
}