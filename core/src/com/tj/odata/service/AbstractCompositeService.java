package com.tj.odata.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odata4j.producer.QueryInfo;

import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractCompositeService implements CompositeService {
	private Map<Class<?>, Service<?>> services;

	public AbstractCompositeService(Collection<Class<?>> classes) {
		services = new HashMap<Class<?>, Service<?>>();
		init(classes);
	}

	protected AbstractCompositeService() {
		services = new HashMap<Class<?>, Service<?>>();
	}

	protected final void init(Collection<Class<?>> classes) {
		for (Class<?> clazz : classes) {
			services.put(clazz, buildServiceForClass(clazz));
		}
	}

	protected Map<Class<?>, Service<?>> getServices() {
		return services;
	}

	protected abstract <T> Service<T> buildServiceForClass(Class<T> type);

	public Service getServiceForObject(Class<?> type) {
		Service serv = services.get(type);
		if (serv == null) {
			throw new IllegalArgumentException("No service for type: " + type.getSimpleName());
		}
		return serv;
	}

	public void addService(Class<?> clazz, Service<?> service) {
		services.put(clazz, service);
	}

	public void addService(Collection<Class<?>> clazz, Service<?> service) {
		for (Class<?> c : clazz) {
			addService(c, service);
		}
	}

	@Override
	public Object createEntity(Class<?> type, RequestContext request, ResponseContext response, Object object) {
		return getServiceForObject(object.getClass()).createEntity(type, request, response, object);
	}

	@Override
	public Object mergeEntity(Class<?> type, RequestContext request, ResponseContext response, Object object,
			KeyMap keys) {
		return getServiceForObject(object.getClass()).mergeEntity(type, request, response, object, keys);
	}

	@Override
	public Object updateEntity(Class<?> type, RequestContext request, ResponseContext response, Object object,
			KeyMap keys) {
		return getServiceForObject(object.getClass()).updateEntity(type, request, response, object, keys);
	}

	@Override
	public Object deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return getServiceForObject(type).deleteEntity(type, request, response, keys);
	}

	@Override
	public Object getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return getServiceForObject(type).getEntity(type, request, response, keys);
	}

	@Override
	public Collection<Object> getEntities(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return getServiceForObject(type).getEntities(type, request, response, keys, info);
	}

	@Override
	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return getServiceForObject(type).getEntitiesCount(type, request, response, keys, info);
	}

	@Override
	public Collection<Class<?>> getTypes() {
		return services.keySet();
	}

	@Override
	public Class<Object> getServiceType() {
		return Object.class;
	}

	public static Class<?> getTypeOfService(Class<? extends Service> serviceClass) {
		ParameterizedType pt = (ParameterizedType) serviceClass.getGenericSuperclass();
		while (serviceClass != Service.class) {
			serviceClass = (Class<? extends Service>) serviceClass.getSuperclass();
			pt = (ParameterizedType) serviceClass.getGenericSuperclass();
		}
		return (Class<?>) pt.getActualTypeArguments()[0];
	}

	public static Collection<Class<?>> getServiceTypesForClass(Class<?> clazz) {
		Type[] interfaces = clazz.getGenericInterfaces();
		List<Class<?>> ret = new ArrayList<Class<?>>();
		for (Type type : interfaces) {
			if (type instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) type;
				Class<?> rawType = (Class<?>) pt.getRawType();
				if (Service.class.isAssignableFrom((Class<?>) rawType)) {
					// if (pt.getRawType() == Service.class) {
					Class<?> toAdd = getTypeOfService((Class<? extends Service>) rawType);
					ret.add(toAdd);
				}
			}
		}
		return ret;
	}

}
