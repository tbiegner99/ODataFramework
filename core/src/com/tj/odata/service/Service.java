package com.tj.odata.service;

import java.util.Collection;

import org.odata4j.producer.QueryInfo;
import org.springframework.transaction.annotation.Transactional;

import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
@Transactional
public interface Service<T> {

	public Class<? extends T> getServiceType();

	public T linkNewEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap objectKey,String property,Object newLink);

	public T createEntity(Class<?> type, RequestContext request, ResponseContext response, T object);

	public T mergeEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys);

	public T updateEntity(Class<?> type, RequestContext request, ResponseContext response, T object, KeyMap keys);

	public T deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys);

	public T getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys);

	public Collection<T> getEntities(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info);

	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info);


	// count
	// public Integer getEntitiesCount(RequestContext request, ResponseContext
	// response, KeyMap keys, QueryInfo info);
	// public Integer getPropertyCount(RequestContext request, ResponseContext
	// response, KeyMap keys, QueryInfo info);
	// For BatchRequests
}
