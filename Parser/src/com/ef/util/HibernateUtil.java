package com.ef.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * The {@code HibernateUtil} class acts as a factory for the hibernate sessions
 * <p>
 * 
 * @author Pavan Reddy
 * @version 1.0
 */
public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			return new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}
}