package de.trispeedys.resourceplanning.messaging;

import java.util.List;

import de.trispeedys.resourceplanning.entity.Position;

public class ProposePositionsMailTemplate extends AbstractMailTemplate
{
    private List<Position> positions;

    public ProposePositionsMailTemplate(List<Position> aPositions)
    {
        super();
        this.positions = aPositions;
    }
    
    public String getBody()
    {
        return "123";
    }
}