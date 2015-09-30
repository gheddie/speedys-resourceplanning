package de.trispeedys.resourceplanning;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;

public class HibernateUtil {
	private static final SessionFactory sessionFactory = buildSessionFactory();

	@SuppressWarnings("deprecation")
	private static SessionFactory buildSessionFactory() {
		try {
			return new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}

	public static void clearAll() {
	    clearTable("event_position");
	    clearTable("event_commitment");
	    clearTable("position");
	    clearTable("domain");	    	    
		clearTable("helper");		
		clearTable("event");
		clearTable("message_queue");
	}

	private static void clearTable(String tableName) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "delete from " + tableName;

		session.createSQLQuery(queryString).executeUpdate();
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
    public static <T> T persistSimple(AbstractDbObject entity) {
		Transaction tx = null;
		Session session = getSessionFactory().openSession();
		tx = session.beginTransaction();
		session.save(entity);
		tx.commit();
		session.close();
		return (T) entity;
	}

    @SuppressWarnings("unchecked")
    public static <T> T findById(Class<? extends AbstractDbObject> entityClass, Long primaryKeyValue)
    {
        return (T) fetchResults("FROM " + entityClass.getSimpleName() + " WHERE id = " + primaryKeyValue).get(0);
    }
    
    @SuppressWarnings("rawtypes")
    public static List<?> fetchResults(String qryString, HashMap<String, Object> parameters)
    {
        Session session = getSessionFactory().openSession();
        Query q =
                session.createQuery(qryString);
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
    
    public static List<?> fetchResults(String qryString)
    {
        return fetchResults(qryString, null);
    }

    public static List<?> fetchResults(String qryString, String paramaterName, Object paramaterObject)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramaterName, paramaterObject);
        return fetchResults(qryString, parameters);
    }

    public static List<?> fetchResults(Class<? extends AbstractDbObject> clazz)
    {
        return fetchResults("FROM " + clazz.getSimpleName());
    }    
}
