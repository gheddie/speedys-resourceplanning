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
	    clearTable("helper_assignment");
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
}
