package de.trispeedys.resourceplanning.entity.builder;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionBuilder extends AbstractEntityBuilder<Position>
{
    private String description;
    
    private int minimalAge;

    private Domain domain;

    private boolean authorityOverride;
    
    public PositionBuilder withDomain(Domain aDomain)
    {
        domain = aDomain;
        return this;
    }

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
    
    public PositionBuilder withAuthorityOverride(boolean anAuthorityOverride)
    {
        authorityOverride = anAuthorityOverride;
        return this;
    }
    
    public Position build()
    {
        Position position = new Position();
        position.setDescription(description);
        position.setMinimalAge(minimalAge);
        position.setDomain(domain);
        position.setAuthorityOverride(authorityOverride);
        return position;
    }
}