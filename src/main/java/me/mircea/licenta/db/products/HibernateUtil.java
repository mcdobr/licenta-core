package me.mircea.licenta.db.products;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
	private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
	private static final SessionFactory sessionFactory = buildSessionFactory();
	
	private HibernateUtil() {}
	
	private static SessionFactory buildSessionFactory() {
		try {
			return new Configuration().configure().buildSessionFactory();
		} catch (Exception e) {
			logger.error("Could not configure Hibernate {}", e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
