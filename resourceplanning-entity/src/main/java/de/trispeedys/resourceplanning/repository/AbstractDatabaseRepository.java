package de.trispeedys.resourceplanning.repository;

public interface AbstractDatabaseRepository<T extends AbstractDatabaseRepository<T>>
{
    public void createDataSource();
}