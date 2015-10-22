package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.PositionDatasource;
import de.trispeedys.resourceplanning.entity.Position;

public class PositionRepository implements AbstractDatabaseRepository<PositionRepository>
{
    private PositionDatasource datasource;

    public Position findPositionByPositionNumber()
    {
        return (Position) datasource.findAll().get(0);       
    }

    public void createDataSource()
    {
        datasource = new PositionDatasource();        
    }
}