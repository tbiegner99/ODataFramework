package com.tj.dao.hibernate;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.odata4j.producer.QueryInfo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.security.SecurityManager;
<<<<<<< HEAD

@Transactional(propagation = Propagation.REQUIRED)
=======
@Transactional(propagation=Propagation.REQUIRED)
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
@org.springframework.stereotype.Service
public class SecurityAwareHibernateDAOService<T> extends AbstractHibernateDAOService<T> {

	public SecurityAwareHibernateDAOService(Class<T> type) {
<<<<<<< HEAD
		super(new GenericHibernateDAO<T>(type), null);
	}

	public SecurityAwareHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact), fact);
	}

	public void setFactory(SessionFactory factory) {
		super.setFactory(factory);
		((GenericHibernateDAO<T>) this.getDAO()).setFactory(factory);
=======
		super(new GenericHibernateDAO<T>(type),null);
	}

	public SecurityAwareHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact),fact);
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public T createEntity(Class<?> type, RequestContext request, ResponseContext response, T object) {
<<<<<<< HEAD
		return ((GenericHibernateDAO<T>) getDAO()).createEntity(object,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
=======
		return ((GenericHibernateDAO<T>)getDAO()).createEntity(object,(SecurityManager<T, ?>)request.getSecurityManager(),request.getUser());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public T mergeEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
<<<<<<< HEAD
		return ((GenericHibernateDAO<T>) getDAO()).getEntity(keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
=======
		return ((GenericHibernateDAO<T>)getDAO()).getEntity(keys,(SecurityManager<T, ?>)request.getSecurityManager(),request.getUser());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public T updateEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
<<<<<<< HEAD
		return ((GenericHibernateDAO<T>) getDAO()).updateEntity(object, keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
=======
		return ((GenericHibernateDAO<T>)getDAO()).updateEntity(object, keys,(SecurityManager<T, ?>)request.getSecurityManager(),request.getUser());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public T deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
<<<<<<< HEAD
		return ((GenericHibernateDAO<T>) getDAO()).deleteEntity(keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
=======
		return ((GenericHibernateDAO<T>)getDAO()).deleteEntity(keys,(SecurityManager<T, ?>) request.getSecurityManager(),request.getUser());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public T getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
<<<<<<< HEAD
		return ((GenericHibernateDAO<T>) getDAO()).getEntity(keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
=======
		return ((GenericHibernateDAO<T>)getDAO()).getEntity(keys,(SecurityManager<T, ?>)request.getSecurityManager(),request.getUser());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public Collection<T> getEntities(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
<<<<<<< HEAD
		return ((GenericHibernateDAO<T>) getDAO()).getEntities(info,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
=======
		return ((GenericHibernateDAO<T>)getDAO()).getEntities(info,(SecurityManager<T, ?>)request.getSecurityManager(),request.getUser());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
<<<<<<< HEAD
		return (long) ((GenericHibernateDAO<T>) getDAO()).countEntities(info,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
=======
		return (long) ((GenericHibernateDAO<T>)getDAO()).countEntities(info,(SecurityManager<T, ?>)request.getSecurityManager(),request.getUser());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}
}
