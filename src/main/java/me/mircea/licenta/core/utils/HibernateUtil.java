package me.mircea.licenta.core.utils;

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
		} catch (Exception ex) {
			logger.error("Could not configure Hibernate");
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
