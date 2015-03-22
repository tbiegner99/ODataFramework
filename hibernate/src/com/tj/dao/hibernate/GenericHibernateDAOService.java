package com.tj.dao.hibernate;

import org.hibernate.SessionFactory;

import com.tj.odata.service.GenericDAOService;

public class GenericHibernateDAOService<T> extends GenericDAOService<T> {

	public GenericHibernateDAOService(Class<T> type) {
		super(new GenericHibernateDAO<T>(type));
	}

	public GenericHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact));
	}

}
