package com.tj.odata.service;

import java.lang.reflect.Field;
import java.util.Collection;

import org.odata4j.producer.QueryInfo;
import org.springframework.transaction.annotation.Transactional;

import com.tj.dao.DAOBase;
import com.tj.dao.SecurityAwareDAO;
import com.tj.exceptions.IllegalRequestException;
import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.util.ReflectionUtil;
import com.tj.security.SecurityManager;
import com.tj.security.user.User;

@Transactional
public class SecurityAwareDAOService<T> extends AbstractService<T> implements Service<T> {
	private SecurityAwareDAO<T> dao;

	public SecurityAwareDAOService(SecurityAwareDAO<T> dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T createEntity(Class<?> type, RequestContext request, ResponseContext response, T object) {
		return dao.createEntity(object, (SecurityManager<T, ?>) request.getSecurityManager(type), request.getUser());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T mergeEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
		return dao.getEntity(keys, (SecurityManager<T, ?>) request.getSecurityManager(type), request.getUser());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T updateEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
		return dao.updateEntity(object, keys, (SecurityManager<T, ?>) request.getSecurityManager(type),
				request.getUser());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return dao.deleteEntity(keys, (SecurityManager<T, ?>) request.getSecurityManager(type), request.getUser());
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return dao.getEntity(keys, (SecurityManager<T, ?>) request.getSecurityManager(type), request.getUser());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getEntities(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return dao.getEntities(request.getContextObjectOfType(QueryInfo.class),
				(SecurityManager<T, ?>) request.getSecurityManager(type), request.getUser());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return (long) dao.countEntities(request.getContextObjectOfType(QueryInfo.class),
				(SecurityManager<T, ?>) request.getSecurityManager(type), request.getUser());
	}

	public DAOBase<T> getDAO() {
		return dao;
	}

	@Override
	public Class<? extends T> getServiceType() {
		return dao.getDAOType();
	}

	@Override
	public T linkNewEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap objectKey,
			String property, Object newLink) {
		try {
			SecurityManager<T, ?> security = (SecurityManager<T, ?>) request.getSecurityManager(type);
			User user = request.getUser();
			T object = dao.getEntity(objectKey, security, user);
			Field f = ReflectionUtil.getFieldForType(object.getClass(), property);
			if (Collection.class.isAssignableFrom(f.getType())) {
				((Collection) ReflectionUtil.invokeGetter(object, property)).add(newLink);
			} else {
				ReflectionUtil.invokeSetter(object, property, newLink);
			}
			dao.updateEntity(object, objectKey, security, user);
			return object;
		} catch (NoSuchFieldException e) {
			throw new IllegalRequestException("");
		}
	}

}
