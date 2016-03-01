package com.tj.dao.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.odata4j.producer.QueryInfo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.security.SecurityManager;

@Transactional(propagation = Propagation.REQUIRED)
@org.springframework.stereotype.Service
public class SecurityAwareHibernateDAOService<T> extends AbstractHibernateDAOService<T> {

	public SecurityAwareHibernateDAOService(Class<T> type) {
		super(new GenericHibernateDAO<T>(type), null);
	}

	public SecurityAwareHibernateDAOService(Class<T> type, SessionFactory fact) {
		super(new GenericHibernateDAO<T>(type, fact), fact);
	}

	public SecurityAwareHibernateDAOService(Class<T> type, Session session) {
		super(new GenericHibernateDAO<T>(type, session), null);
	}

	@Override
	public T createEntity(Class<?> type, RequestContext request, ResponseContext response, T object) {
		return ((GenericHibernateDAO<T>) getDAO()).createEntity(object,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
	}

	@Override
	public T mergeEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
		return ((GenericHibernateDAO<T>) getDAO()).getEntity(keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
	}

	@Override
	public T updateEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
		return ((GenericHibernateDAO<T>) getDAO()).updateEntity(object, keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
	}

	@Override
	public T deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return ((GenericHibernateDAO<T>) getDAO()).deleteEntity(keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
	}

	@Override
	public T getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return ((GenericHibernateDAO<T>) getDAO()).getEntity(keys,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
	}

	@Override
	public Collection<T> getEntities(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return ((GenericHibernateDAO<T>) getDAO()).getEntities(info,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
	}

	@Override
	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return (long) ((GenericHibernateDAO<T>) getDAO()).countEntities(info,
				(SecurityManager<T, ?>) request.getSecurityManager(), request.getUser());
	}
}
