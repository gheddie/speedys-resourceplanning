package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;

public class RepositoryProvider
{
    private static RepositoryProvider instance;
    
    private HashMap<Class<? extends DatabaseRepository>, DatabaseRepository> repositoryCache;
    
    private RepositoryProvider()
    {
        repositoryCache = new HashMap<Class<? extends DatabaseRepository>, DatabaseRepository>();
        registerRepositories();
    }

    private void registerRepositories()
    {
        registerRepository(PositionRepository.class);
        registerRepository(DomainRepository.class);
        registerRepository(EventPositionRepository.class);
        registerRepository(EventRepository.class);
    }

    private void registerRepository(Class<? extends DatabaseRepository> clazz)
    {
        try
        {
            DatabaseRepository repositoryInstance = clazz.newInstance();
            repositoryInstance.createDataSource();
            repositoryCache.put(clazz, repositoryInstance);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }        
    }

    private static RepositoryProvider getInstance()
    {
        if (RepositoryProvider.instance == null)
        {
            RepositoryProvider.instance = new RepositoryProvider();
        }
        return RepositoryProvider.instance;
    }
    
    private <T extends DatabaseRepository<T>> T getRepositoryForClass(Class<? extends AbstractDbObject> entityClass)
    {
        if (entityClass == null)
        {
            return null;
        }
        return (T) repositoryCache.get(entityClass);        
    }
    
    public static <T extends DatabaseRepository<T>> T getRepository(Class<T> repositoryClass)
    {
        T repositoryForClass = (T) getInstance().getRepositoryForClass((Class<? extends AbstractDbObject>) repositoryClass);        
        return repositoryForClass;
    }    
}