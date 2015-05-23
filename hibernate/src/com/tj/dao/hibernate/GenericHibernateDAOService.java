package com.tj.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
<<<<<<< HEAD

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class GenericHibernateDAOService<T> extends AbstractHibernateDAOService<T> {
	public GenericHibernateDAOService(Class<T> type) {
		super(new GenericHibernateDAO<T>(type), null);
	}

	public GenericHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact), fact);
	}

	public void setFactory(SessionFactory factory) {
		super.setFactory(factory);
		((GenericHibernateDAO<T>) this.getDAO()).setFactory(factory);
	}
=======
@Service
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class GenericHibernateDAOService<T> extends AbstractHibernateDAOService<T> {
	public GenericHibernateDAOService(Class<T> type) {
		super(new GenericHibernateDAO<T>(type),null);
	}

	public GenericHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact),fact);
	}

>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
}
