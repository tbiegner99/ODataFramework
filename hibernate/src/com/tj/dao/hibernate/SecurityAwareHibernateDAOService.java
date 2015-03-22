package com.tj.dao.hibernate;

import org.hibernate.SessionFactory;

import com.tj.odata.service.GenericDAOService;

public class SecurityAwareHibernateDAOService<T> extends GenericDAOService<T> {

	public SecurityAwareHibernateDAOService(Class<T> type) {
		super(new GenericHibernateDAO<T>(type));
	}

	public SecurityAwareHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact));
	}

}
