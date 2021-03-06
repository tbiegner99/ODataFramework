package com.tj.dao.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class GenericHibernateDAOService<T> extends AbstractHibernateDAOService<T> {
	public GenericHibernateDAOService(Class<T> type) {
		super(new GenericHibernateDAO<T>(type), null);
	}

	public GenericHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact), fact);
	}

	public GenericHibernateDAOService(Class<T> type, Session session) {
		super(new GenericHibernateDAO<T>(type, session), null);
	}

	public void setFactory(SessionFactory factory) {
		super.setFactory(factory);
		((GenericHibernateDAO<T>) getDAO()).setFactory(factory);
	}

}
