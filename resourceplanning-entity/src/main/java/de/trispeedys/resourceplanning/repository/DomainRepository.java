package de.trispeedys.resourceplanning.repository;

import de.trispeedys.resourceplanning.datasource.DomainDatasource;
import de.trispeedys.resourceplanning.entity.Domain;

public class DomainRepository implements DatabaseRepository<DomainRepository>
{
    private DomainDatasource datasource;

    public Domain findDomainByNumber(int domainNumber)
    {
        return datasource.findSingle(Domain.ATTR_DOMAIN_NUMBER, domainNumber);
    }
    
    public void createDataSource()
    {
        datasource = new DomainDatasource();
    }
}