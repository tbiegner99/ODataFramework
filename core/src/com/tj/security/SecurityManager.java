package com.tj.security;

import java.util.Collection;
import java.util.Map;

import com.tj.dao.filter.Filter;

public interface SecurityManager<T, E extends User> {

	boolean canReadEntity(Class<? extends T> subType, E u);

	boolean canWriteEntity(T entity, E u);

	boolean canUpdateEntity(T entity, E u);

	boolean canDeleteEntity(Class<? extends T> subType, E u);

	boolean canAccessProperty(T entity, String property, E u);

	boolean canReadProperty(T entity, String property, E u);

	boolean canWriteProperty(T entity, String property, E u);

	boolean canUpdateProperty(T entity, String property, E u);

	Object getPropertyValueForCreate(T entity, Object supplied, String property, E u);

	Object getPropertyValueForUpdate(T entity, Object supplied, String property, E u);

	Object getPropertyValueForRead(T entity, Object supplied, String property, E u);

	Collection<Filter> getUserLevelFilters(Class<T> clazz, E u);

	Map<String, Object> getFilterParameters(Class<T> clazz, E u);
}
