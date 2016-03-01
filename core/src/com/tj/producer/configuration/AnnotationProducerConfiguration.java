package com.tj.producer.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.cfg.NotYetImplementedException;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmGenerator;

import com.tj.odata.functions.FunctionInfo;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.producer.GenericEdmGenerator;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.annotations.CreateEntity;
import com.tj.producer.annotations.GetEntities;
import com.tj.producer.annotations.GetEntity;
import com.tj.producer.annotations.Header;
import com.tj.producer.annotations.entity.Key;
import com.tj.producer.invoker.ArgumentResolver;
import com.tj.producer.invoker.HeaderArgumentResolver;
import com.tj.producer.invoker.RequestContextResolver;
import com.tj.producer.media.MediaResolverFactory;
import com.tj.security.CompositeSecurityManager;

public class AnnotationProducerConfiguration implements ProducerConfiguration {
	private Map<String, Class<?>> edmTypes;
	private Map<Class<?>, Map<Action, Invoker>> config;
	private Map<Class<?>, Map<String, Class<?>>> keyTypes;
	private Map<FunctionName, FunctionInfo> functions;
	private Map<FunctionName, Invoker> functionInvokers;
	private Map<Class<?>, MediaResolverFactory> mediaEntities;
	private int maxResults = 500;
	private EdmGenerator edm;
	private boolean validationEnabled = true;
	private EdmDataServices metadata;
	private CompositeSecurityManager securityManager;

	public AnnotationProducerConfiguration(List<Object> services) {
		edmTypes = new HashMap<String, Class<?>>();
		config = new HashMap<Class<?>, Map<Action, Invoker>>();
		keyTypes = new HashMap<Class<?>, Map<String, Class<?>>>();
		functions = new HashMap<FunctionName, FunctionInfo>();
		functionInvokers = new HashMap<FunctionName, Invoker>();
		mediaEntities = new HashMap<Class<?>, MediaResolverFactory>();
		setUpConfig(services);
		refreshMetadata();
	}

	public AnnotationProducerConfiguration(List<Object> services, CompositeSecurityManager securityManager) {
		this(services);
		this.securityManager = securityManager;
	}

	public boolean doValidate() {
		return validationEnabled;
	}

	public boolean isValidationEnabled() {
		return validationEnabled;
	}

	public void setValidationEnabled(boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
	}

	@Override
	public Class<?> getEntitySetClass(String entitySetName) {
		return edmTypes.get(entitySetName);
	}

	@Override
	public Map<String, Class<?>> getKeysMap(String entitySetName) {
		if (edmTypes.containsKey(entitySetName)) {
			return keyTypes.get(edmTypes.get(entitySetName));
		}
		return null;
	}

	public Collection<Class<?>> getKeys(String entitySetName) {
		if (edmTypes.containsKey(entitySetName)) {
			return keyTypes.get(edmTypes.get(entitySetName)).values();
		}
		return null;
	}

	public Invoker getInvoker(String entitySetName, Action a) {
		if (edmTypes.containsKey(entitySetName)) {
			Map<Action, Invoker> methodMap = config.get(edmTypes.get(entitySetName));
			if (methodMap != null) {
				return methodMap.get(a);
			}
		}
		return null;
	}

	@Override
	public Collection<Class<?>> getEntityTypes() {
		return edmTypes.values();
	}

	private void setUpConfig(List<Object> services) {
		for (Object o : services) {
			setUpObject(o);
			MediaResolverFactory factory = MediaResolverFactory.createFromClass(o.getClass());
			if (factory != null) {
				mediaEntities.put(o.getClass(), factory);
			}
		}
	}

	private void setUpObject(Object o) {
		Method[] m = o.getClass().getMethods();
		for (Method meth : m) {
			processMethod(o, meth);
		}
	}

	private void getKeyTypes(Class<?> clazz) {
		if (keyTypes.containsKey(clazz)) {
			return;
		}
		Map<String, Class<?>> keys = new HashMap<String, Class<?>>();
		keyTypes.put(clazz, keys);
		boolean primitiveFound = false;
		for (Field f : clazz.getDeclaredFields()) {
			if (f.isAnnotationPresent(Key.class)) {
				if (f.getType().isPrimitive()) {
					primitiveFound = true;
					keys.put(f.getName(), f.getType());
				} else {
					if (!primitiveFound) {
						keys.put(" ", f.getType());
					}
					return;
				}
			}
		}
	}

	private void processMethod(Object o, Method m) {
		if (m.isAnnotationPresent(GetEntity.class)) {
			GetEntity an = m.getAnnotation(GetEntity.class);
			getKeyTypes(an.type());
			edmTypes.put(an.type().getSimpleName(), an.type());
			addAction(an.type(), Action.GET, createInvoker(o, m, an.type()));
		} else if (m.isAnnotationPresent(CreateEntity.class)) {
			CreateEntity an = m.getAnnotation(CreateEntity.class);
			getKeyTypes(an.type());
			edmTypes.put(an.type().getSimpleName(), an.type());
			addAction(an.type(), Action.CREATE, createInvoker(o, m, an.type()));
		} else if (m.isAnnotationPresent(GetEntities.class)) {

		}

	}

	private void addAction(Class<?> clazz, Action a, Invoker i) {
		if (!config.containsKey(clazz)) {
			config.put(clazz, new HashMap<Action, Invoker>());
		}
		config.get(clazz).put(a, i);
	}

	private Invoker createInvoker(Object o, Method m, Class<?> type) {
		Class<?>[] parameterTypes = m.getParameterTypes();
		List<ArgumentResolver> resolvers = new ArrayList<ArgumentResolver>();
		int i = 0;
		for (Annotation[] params : m.getParameterAnnotations()) {
			Class<?> atype = parameterTypes[i++];
			boolean isHeader = false;
			for (Annotation ann : params) {
				if (ann.annotationType() == Header.class) {
					String headerName = ((Header) ann).name();
					resolvers.add(new HeaderArgumentResolver(headerName, atype));
					isHeader = true;
				}
			}
			if (!isHeader) {
				resolvers.add(new RequestContextResolver(atype));
			}
		}
		return new Invoker(o, m, resolvers);
	}

	@Override
	public Object invoke(String entitySet, Action a, RequestContext request, ResponseContext response) {
		if (a == Action.GET_MEDIA) {
			return getMediaStream();
		}
		Invoker i = methodToCall(entitySet, a);
		return i.invoke(request);
	}

	public Invoker methodToCall(String type, Action meth) {
		return methodToCall(edmTypes.get(type), meth);
	}

	public Invoker methodToCall(Class<?> type, Action meth) {
		if (config.containsKey(type) && config.get(type).containsKey(meth)) {
			return config.get(type).get(meth);
		}
		throw new RuntimeException();
	}

	public static class Invoker {
		private Object object;
		private Method method;
		private List<ArgumentResolver> resolvers;

		public Invoker(Object obj, Method m, List<ArgumentResolver> resolvers) {
			this.object = obj;
			method = m;
			this.resolvers = resolvers;
		}

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Object invoke(RequestContext context) {
			try {
				Object[] args = new Object[resolvers.size()];
				int i = 0;
				for (ArgumentResolver r : resolvers) {
					args[i++] = r.resolveArgument(context);
				}
				return method.invoke(object, args);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void close() {
	}

	@Override
	public Collection<FunctionName> getSupportedFunctions() {
		return functions.keySet();
	}

	@Override
	public boolean hasFunction(FunctionName name) {
		return functions.containsKey(name);
	}

	@Override
	public FunctionInfo getFunctionInfo(FunctionName functionName) {
		return functions.get(functionName);
	}

	@Override
	public Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response, ProducerConfiguration config) {
		if (!hasFunction(name)) {
			throw new IllegalArgumentException("Function not found: " + name.getName());
		}
		return functionInvokers.get(name).invoke(request);
	}

	private Object getMediaStream() {
		throw new NotYetImplementedException("Media entities not yet supported");
	}

	@Override
	public boolean isMediaEntity(Class<?> clazz) {
		return mediaEntities.containsKey(clazz);
	}

	@Override
	public MediaResolverFactory getMediaResolverFactory(Class<?> clazz) {
		return mediaEntities.get(clazz);
	}

	@Override
	public int getMaxResults() {
		return maxResults;
	}

	@Override
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	@Override
	public CompositeSecurityManager getSecurityManager() {
		return securityManager;
	}

	@Override
	public EdmGenerator getEdmGenerator() {
		return edm;
	}

	@Override
	public EdmDataServices getMetadata() {
		if (metadata == null) {
			refreshMetadata();
		}
		return metadata;
	}

	@Override
	public EdmDataServices refreshMetadata() {
		edm = new GenericEdmGenerator(this);
		metadata = edm.generateEdm(null).build();
		return getMetadata();
	}

	@Override
	public void setSecurityManager(CompositeSecurityManager securityManager) {
		this.securityManager = securityManager;

	}

}
