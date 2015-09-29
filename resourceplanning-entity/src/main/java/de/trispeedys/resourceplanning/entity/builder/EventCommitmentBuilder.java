package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;

public class EventCommitmentBuilder extends AbstractEntityBuilder<EventCommitment>
{
    private Helper helper;
    
    private Position position;

    private Event event;

    public EventCommitmentBuilder withHelper(Helper aHelper)
    {
        helper = aHelper;
        return this;
    }

    public EventCommitmentBuilder withPosition(Position aPosition)
    {
        position = aPosition;
        return this;
    }

    public EventCommitmentBuilder withEvent(Event aEvent)
    {
        event = aEvent;
        return this;
    }
    
    public EventCommitment build()
    {
        EventCommitment eventCommitment = new EventCommitment();
        eventCommitment.setEvent(event);
        eventCommitment.setHelper(helper);
        eventCommitment.setPosition(position);
        return eventCommitment;
    }
}