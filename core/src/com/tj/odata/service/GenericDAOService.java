package com.tj.odata.service;

import java.lang.reflect.Field;
import java.util.Collection;

import org.odata4j.producer.QueryInfo;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tj.dao.DAOBase;
import com.tj.exceptions.IllegalRequestException;
import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.util.ReflectionUtil;

@Transactional(propagation=Propagation.REQUIRED)
@org.springframework.stereotype.Service
public class GenericDAOService<T> extends AbstractService<T> implements Service<T> {
	private DAOBase<T> dao;

	public GenericDAOService(DAOBase<T> dao) {
		this.dao = dao;
	}

	@Override
	public T createEntity(Class<?> type, RequestContext request, ResponseContext response, T object) {
		return dao.createEntity(object);
	}

	@Override
	public T mergeEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
		return dao.getEntity(keys);
	}

	@Override
	public T updateEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys) {
		return dao.updateEntity(object, keys);
	}

	@Override
	public T deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return dao.deleteEntity(keys);
	}

	@Override
	public T getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return dao.getEntity(keys);
	}

	@Override
	public Collection<T> getEntities(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return dao.getEntities(info);
	}

	@Override
	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return (long) dao.countEntities(info);
	}

	public DAOBase<T> getDAO() {
		return dao;
	}

	@Override
	public Class<? extends T> getServiceType() {
		return dao.getDAOType();
	}

	@Override
	public T linkNewEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap objectKey, String property, Object newLink) {
		try {
			T object=getEntity(type, request, response, objectKey);
			Field f=ReflectionUtil.getFieldForType(object.getClass(), property);
			if(Collection.class.isAssignableFrom(f.getType())) {
				((Collection)ReflectionUtil.invokeGetter(object, property)).add(newLink);
			} else {
				ReflectionUtil.invokeSetter(object, property, newLink);
			}
			updateEntity(type, request, response, object, objectKey);
			return object;
		} catch (NoSuchFieldException e) {
			throw new IllegalRequestException("");
		}
	}
}
