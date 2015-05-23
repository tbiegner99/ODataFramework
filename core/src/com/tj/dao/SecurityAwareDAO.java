package com.tj.dao;

import java.util.Collection;
import java.util.Map;

import org.odata4j.producer.QueryInfo;

import com.tj.producer.KeyMap;
import com.tj.security.SecurityManager;
import com.tj.security.user.User;
/**
 * This extension to the base dao allows implementors to take a security context that may be used to generate query.
 * Optionally, implementors may also implement operations specific for situations without a security context
 * @author tbiegner
 *
 * @param <T>
 */
public interface SecurityAwareDAO<T> extends DAOBase<T> {
	T createEntity(T entity, SecurityManager<T, ?> manager, User user);

	Collection<T> createEntityBatch(Collection<T> entities, SecurityManager<T, ?> manager, User user);

	T deleteEntity(KeyMap entityKey, SecurityManager<T, ?> manager, User user);

	Collection<T> deleteEntityBatch(Collection<KeyMap> entityKeys, SecurityManager<T, ?> manager, User user);

	Collection<T> deleteEntityBatchFilter(QueryInfo entityKeys, SecurityManager<T, ?> manager, User user);

	T updateEntity(T entity, KeyMap entityKey, SecurityManager<T, ?> manager, User user);

	Collection<T> updateEntityBatch(Map<KeyMap, T> entities, SecurityManager<T, ?> manager, User user);

	Collection<T> updateEntityBatchFilter(QueryInfo entityKeys, SecurityManager<T, ?> manager, User user);

	T getEntity(KeyMap entity, SecurityManager<T, ?> manager, User user);

	T getSingleEntity(QueryInfo info, SecurityManager<T, ?> manager, User user);

	Collection<T> getEntities(QueryInfo info, SecurityManager<T, ?> manager, User user);

	long countEntities(QueryInfo filters, SecurityManager<T, ?> manager, User user);
}
