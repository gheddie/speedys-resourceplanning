package de.trispeedys.resourceplanning;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;

public class HibernateUtil
{
	private static final SessionFactory sessionFactory = buildSessionFactory();

	@SuppressWarnings("deprecation")
	private static SessionFactory buildSessionFactory()
	{
		try
		{
			return new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex)
		{
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public static void shutdown()
	{
		getSessionFactory().close();
	}
	
	public static void clearAll()
	{
	    clearTable("event_commitment");
	    clearTable("helper");
	    clearTable("position");
	    clearTable("event_occurence");
	}

    private static void clearTable(String tableName)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        String queryString = "delete from " + tableName;

        session.createSQLQuery(queryString).executeUpdate();
        tx.commit();
        session.close();
    }
    
    public static AbstractDbObject persistSimple(AbstractDbObject entity)
    {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
        session.save(entity);
        tx.commit();
        session.close();
        return entity;
    }
}
