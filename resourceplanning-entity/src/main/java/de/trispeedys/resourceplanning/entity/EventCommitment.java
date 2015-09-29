package de.trispeedys.resourceplanning.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.EventCommitmentState;

@Entity
@Table(name = "event_commitment")
public class EventCommitment extends AbstractDbObject
{
    @OneToOne(fetch = FetchType.EAGER)
    private Helper helper;
    
    @Column(name = "helper_id", insertable = false, updatable = false)
    private Long helperId;
    
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;
    
    @OneToOne(fetch = FetchType.EAGER)
    private Position position;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "commitment_state")
    @NotNull
    private EventCommitmentState commitmentState;

    public Helper getHelper()
    {
        return helper;
    }

    public void setHelper(Helper helper)
    {
        this.helper = helper;
    }

    public Event getEvent()
    {
        return event;
    }

    public void setEvent(Event event)
    {
        this.event = event;
    }

    public Position getPosition()
    {
        return position;
    }

    public void setPosition(Position position)
    {
        this.position = position;
    }
    
    public EventCommitmentState getCommitmentState()
    {
        return commitmentState;
    }
    
    public void setCommitmentState(EventCommitmentState commitmentState)
    {
        this.commitmentState = commitmentState;
    }
    
    public Long getHelperId()
    {
        return helperId;
    }
}