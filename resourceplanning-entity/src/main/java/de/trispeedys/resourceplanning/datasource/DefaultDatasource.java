package de.trispeedys.resourceplanning.datasource;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.trispeedys.resourceplanning.HibernateUtil;
import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.MessageQueue;

public class DefaultDatasource<T> implements IDatasource
{
    @SuppressWarnings({
            "rawtypes", "unchecked"
    })
    public <T> List<T> find(String qryString, HashMap<String, Object> parameters)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query q = session.createQuery(qryString);
        if (parameters != null)
        {
            for (String key : parameters.keySet())
            {
                q.setParameter(key, parameters.get(key));
            }
        }
        List result = q.list();
        session.close();
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<T> find(String qryString)
    {
        return (List<T>) find(qryString, null);
    }

    public List<?> find(String qryString, String paramaterName, Object paramaterValue)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramaterName, paramaterValue);
        return (List<?>) find(qryString, parameters);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> find(Class<T> entityClass, String paramaterName, Object paramaterValue)
    {
        return (List<T>) find("FROM " +
                entityClass.getSimpleName() + " WHERE " + paramaterName + " = :" + paramaterName, paramaterName,
                paramaterValue);
    }

    public <T> T saveOrUpdate(T entity)
    {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
        if (((AbstractDbObject) entity).isNew())
        {
            session.save(entity);
        }
        else
        {
            session.update(entity);
        }
        tx.commit();
        session.close();
        return (T) entity;
    }

    public <T> List<T> findAll(Class<T> entityClass)
    {
        return (List<T>) find("FROM " + entityClass.getSimpleName());
    }

    public <T> List<T> find(Class<MessageQueue> entityClass, Object... filters)
    {
        if ((filters == null) || (filters.length == 0))
        {
            // no filters
            return (List<T>) findAll(entityClass);
        }
        if (!(filters.length % 2 == 0))
        {
            // odd number of filters arguments
            return null;
        }
        if (filters.length == 2)
        {
            // one key value pair
            return find(entityClass, filters[0], filters[1]);
        }
        String qryString = "FROM " + entityClass.getSimpleName() + " WHERE (";
        for (int index = 0; index < filters.length; index += 2)
        {
            qryString += filters[index] + " = :" + filters[index];
            if (index < filters.length - 2)
            {
                qryString += " AND ";   
            }            
        }
        qryString += ")";        
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        for (int index = 0; index < filters.length; index += 2)
        {
            parameters.put((String) filters[index], filters[index+1]);
        }
        return find(qryString, parameters);
    }

    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> entityClass, Long primaryKeyValue)
    {
        return (T) find("FROM " + entityClass.getSimpleName() + " WHERE id = " + primaryKeyValue).get(0);
    }
}