package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.EventCommitment;
import de.trispeedys.resourceplanning.entity.EventCommitmentState;
import de.trispeedys.resourceplanning.entity.EventOccurence;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;

public class EventCommitmentBuilder extends AbstractEntityBuilder<EventCommitment>
{
    private Helper helper;
    
    private Position position;

    private EventOccurence eventOccurence;

    private EventCommitmentState commitmentState;

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

    public EventCommitmentBuilder withEventOccurence(EventOccurence aEventOccurence)
    {
        eventOccurence = aEventOccurence;
        return this;
    }
    
    public EventCommitmentBuilder withCommitmentState(EventCommitmentState aCommitmentState)
    {
        commitmentState = aCommitmentState;
        return this;
    }
    
    public EventCommitment build()
    {
        EventCommitment eventCommitment = new EventCommitment();
        eventCommitment.setEventOccurence(eventOccurence);
        eventCommitment.setHelper(helper);
        eventCommitment.setPosition(position);
        eventCommitment.setCommitmentState(commitmentState);
        return eventCommitment;
    }
}