package de.trispeedys.resourceplanning.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "aggregation_relation")
public class AggregationRelation extends AbstractDbObject
{
    private Position position;
    
    private PositionAggregation positionAggregation;
    
    public Position getPosition()
    {
        return position;
    }
    
    public void setPosition(Position position)
    {
        this.position = position;
    }
    
    public PositionAggregation getPositionAggregation()
    {
        return positionAggregation;
    }
    
    public void setPositionAggregation(PositionAggregation positionAggregation)
    {
        this.positionAggregation = positionAggregation;
    }
}