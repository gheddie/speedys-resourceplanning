package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionBuilder extends AbstractEntityBuilder<Position>
{
    private String description;
    
    private int minimalAge;

    public PositionBuilder withDescription(String aDescription)
    {
        description = aDescription;
        return this;
    }
    
    public PositionBuilder withMinimalAge(int aMinimalAge)
    {
        minimalAge = aMinimalAge;
        return this;
    }
    
    public Position build()
    {
        Position position = new Position();
        position.setDescription(description);
        position.setMinimalAge(minimalAge);
        return position;
    }
}