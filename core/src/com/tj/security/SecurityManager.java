package com.tj.security;

import java.util.Collection;
import java.util.Map;

import com.tj.dao.filter.Filter;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.security.user.User;

public interface SecurityManager<T, E extends User> {

	boolean canReadEntity(Class<? extends T> type,E user,ProducerConfiguration appContext);

	boolean canReadEntity(T entity, E u,ProducerConfiguration appContext);

	boolean canWriteEntity(T entity, E u,ProducerConfiguration appContext);

	boolean canUpdateEntity(T entity, E u,ProducerConfiguration appContext);

	boolean canDeleteEntity(Class<? extends T> entity, E u,ProducerConfiguration appContext);

	boolean canAccessProperty(T entity, String property, E u,ProducerConfiguration appContext);

	boolean canReadProperty(T entity, String property, E u,ProducerConfiguration appContext);

	boolean canWriteProperty(T entity, String property, E u,ProducerConfiguration appContext);

	boolean canUpdateProperty(T entity, String property, E u,ProducerConfiguration appContext);

	Object getPropertyValueForCreate(T entity, Object supplied, String property, E u,ProducerConfiguration appContext);

	Object getPropertyValueForUpdate(T entity, Object supplied, String property, E u,ProducerConfiguration appContext);

	Object getPropertyValueForRead(T entity, Object supplied, String property, E u,ProducerConfiguration appContext);

	Collection<Filter> getUserLevelFilters(Class<T> clazz, E u,ProducerConfiguration appContext);

	Map<String, Object> getFilterParameters(Class<T> clazz, E u,ProducerConfiguration appContext);
}
