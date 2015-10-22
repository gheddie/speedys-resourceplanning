package de.trispeedys.resourceplanning.repository;

import java.util.HashMap;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Position;

public class RepositoryProvider
{
    private static RepositoryProvider instance;
    
    private HashMap<Class<? extends AbstractDatabaseRepository>, AbstractDatabaseRepository> repositoryCache;
    
    private RepositoryProvider()
    {
        repositoryCache = new HashMap<Class<? extends AbstractDatabaseRepository>, AbstractDatabaseRepository>();
        registerRepositories();
    }

    private void registerRepositories()
    {
        moo(PositionRepository.class);
    }

    private void moo(Class<? extends AbstractDatabaseRepository> clazz)
    {
        try
        {
            AbstractDatabaseRepository repositoryInstance = clazz.newInstance();
            repositoryInstance.createDataSource();
            repositoryCache.put(clazz, repositoryInstance);
        }
        catch (Exception e)
        {
            // ...
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
    
    private <T extends AbstractDatabaseRepository<T>> T getRepositoryForClass(Class<? extends AbstractDbObject> entityClass)
    {
        if (entityClass == null)
        {
            return null;
        }
        return (T) repositoryCache.get(entityClass);        
    }
    
    public static <T extends AbstractDatabaseRepository<T>> T getRepository(Class<T> repositoryClass)
    {
        T repositoryForClass = (T) getInstance().getRepositoryForClass((Class<? extends AbstractDbObject>) repositoryClass);        
        return repositoryForClass;
    }    
}