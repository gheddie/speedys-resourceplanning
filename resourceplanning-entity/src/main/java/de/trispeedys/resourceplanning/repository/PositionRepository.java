package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.PositionDatasource;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionRepository implements DatabaseRepository<PositionRepository>
{
    private PositionDatasource datasource;

    public Position findPositionByPositionNumber(int positionNumber)
    {
        return (Position) datasource.findSingle(Position.ATTR_POS_NUMBER, positionNumber);       
    }

    public void createDataSource()
    {
        datasource = new PositionDatasource();        
    }
}