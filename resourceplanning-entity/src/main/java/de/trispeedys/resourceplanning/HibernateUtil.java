package de.trispeedys.resourceplanning;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.trispeedys.resourceplanning.entity.AbstractDbObject;
import de.trispeedys.resourceplanning.entity.Helper;

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
		clearTable("event_commitment");
		clearTable("helper");
		clearTable("position");
		clearTable("event_occurence");
		clearTable("message_queue");
	}

	private static void clearTable(String tableName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		String queryString = "delete from " + tableName;

		session.createSQLQuery(queryString).executeUpdate();
		tx.commit();
		session.close();
	}

	public static AbstractDbObject persistSimple(AbstractDbObject entity) {
		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		tx = session.beginTransaction();
		session.save(entity);
		tx.commit();
		session.close();
		return entity;
	}

    @SuppressWarnings("unchecked")
    public static <T> T findById(Class<Helper> entityClass, Long primaryKeyValue)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query qry = session.createQuery("FROM " + entityClass.getSimpleName() + " WHERE id = " + primaryKeyValue);
        Object result = qry.list().get(0);
        session.close();
        return (T) result;
    }
}
