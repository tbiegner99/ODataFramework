package com.tj.dao;

import java.util.Collection;
import java.util.Map;

import org.odata4j.producer.QueryInfo;

import com.tj.producer.KeyMap;
/***
 * The Base interface for all data access object. Implementors may implement basic
 * Crud operations used in odata, as well as optional filter based batch operations.
 * @author tbiegner
 *
 * @param <T>
 */
public interface DAOBase<T> {
	T createEntity(T entity);

	Collection<T> createEntityBatch(Collection<T> entities);

	T deleteEntity(KeyMap entityKey);

	Collection<T> deleteEntityBatch(Collection<KeyMap> entityKeys);

	Collection<T> deleteEntityBatchFilter(QueryInfo entityKeys);

	T updateEntity(T entity, KeyMap entityKey);

	Collection<T> updateEntityBatch(Map<KeyMap, T> entities);

	Collection<T> updateEntityBatchFilter(QueryInfo entityKeys);

	T getEntity(KeyMap entity);

	T getSingleEntity(QueryInfo info);

	Collection<T> getEntities(QueryInfo info);

	long countEntities(QueryInfo filters);

	void close();

	Class<? extends T> getDAOType();
}
