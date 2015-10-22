package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.DomainDatasource;

public class DomainRepository implements DatabaseRepository<DomainRepository>
{
    private DomainDatasource datasource;

    public void createDataSource()
    {
        datasource = new DomainDatasource();
    }

    public String findAll()
    {
        return null;
    }
}