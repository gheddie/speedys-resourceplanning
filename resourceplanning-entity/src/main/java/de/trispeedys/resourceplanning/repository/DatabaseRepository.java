package de.trispeedys.resourceplanning.repository;

public interface DatabaseRepository<T extends DatabaseRepository<T>>
{
    public void createDataSource();
}