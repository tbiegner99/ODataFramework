package com.tj.producer.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.odata4j.producer.QueryInfo;

import com.tj.exceptions.IllegalOperationException;
import com.tj.odata.functions.Function;
import com.tj.odata.functions.FunctionFactory;
import com.tj.odata.functions.FunctionInfo;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.odata.functions.FunctionService;
import com.tj.odata.functions.InvokableFunction;
import com.tj.odata.service.CompositeService;
import com.tj.odata.service.Service;
import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.media.MediaResolverFactory;

public class ServiceProducerConfiguration implements ProducerConfiguration {
	// possilbly expand this to one class for multiple services
	private Map<String, Class<?>> classes;
	private Map<Class<?>, Service<?>> services;
	private Map<FunctionName, InvokableFunction> functionServices;
	private Map<FunctionName, FunctionInfo> functions;
	private Map<Class<?>, MediaResolverFactory> mediaEntities;
	private int maxResults=500;

	public ServiceProducerConfiguration(Collection<? extends Service<?>> services) {
		this();
		setUp(services);
	}

	public ServiceProducerConfiguration(Collection<? extends Service<?>> services,
			Collection<? extends FunctionService> funcServices) {
		this(services);
		setUpServices(funcServices);
	}

	public ServiceProducerConfiguration(Collection<? extends Service<?>> services, FunctionFactory<?>... functions) {
		this(services);
		setUpFunctions(Arrays.asList(functions));
	}

	public ServiceProducerConfiguration(Collection<? extends Service<?>> services, Function... functions) {
		this(services);
		setUpFunctionList(Arrays.asList(functions));
	}

	public ServiceProducerConfiguration() {
		this.services = new HashMap<Class<?>, Service<?>>();
		this.classes = new HashMap<String, Class<?>>();
		this.functions = new HashMap<FunctionName, FunctionInfo>();
		this.functionServices = new HashMap<FunctionName, InvokableFunction>();
		this.mediaEntities = new HashMap<>();
	}

	public void setServices(Collection<? extends Service<?>> services) {
		setUp(services);
	}

	public void setFunctionFactories(FunctionFactory<?>... factory) {
		setUpFunctions(Arrays.asList(factory));
	}

	public void setFunctions(Function... factory) {
		setUpFunctionList(Arrays.asList(factory));
	}

	public void setFunctionServices(Collection<? extends FunctionService> funcServices) {
		setUpServices(funcServices);
	}

	private void setUpServices(Collection<? extends FunctionService> services) {
		for (FunctionService service : services) {
			for (FunctionName name : service.getSupportedFunctions()) {
				functionServices.put(name, service);
				functions.put(name, service.getFunctionInfo(name));
			}
		}

	}

	private void setUpFunctionList(Collection<? extends Function> list) {
		for (Function func : list) {
			// throw exception on duplicates?
			FunctionName name = func.getAliases().getName();
			functionServices.put(name, func);
			functions.put(name, func.getAliases());
		}

	}

	private void setUpFunctions(Collection<? extends FunctionFactory<?>> services) {
		for (FunctionFactory<?> fs : services) {
			for (FunctionName name : fs.getSupportedFunctions()) {
				// throw exception on duplicates?
				functionServices.put(name, fs.getFunction(name));
				functions.put(name, fs.getFunctionInfo(name));
			}
		}
	}

	private void setUpMediaEntity(Class<?> clazz) {
		MediaResolverFactory factory = MediaResolverFactory.createFromClass(clazz);
		if (factory != null) {
			mediaEntities.put(clazz, factory);
		}
	}

	private void setUp(Collection<? extends Service<?>> services2) {
		for (Service<?> s : services2) {
			if (s instanceof CompositeService) {
				CompositeService service = (CompositeService) s;
				for (Class<?> clazz : service.getTypes()) {
					classes.put(clazz.getSimpleName(), clazz);
					services.put(clazz, service);
					setUpMediaEntity(clazz);
				}
				continue;
			} else {
				Class<?> clazz = s.getServiceType();
				classes.put(clazz.getSimpleName(), clazz);
				services.put(clazz, s);
				setUpMediaEntity(clazz);
			}

		}

	}

	@Override
	public <T> Object invoke(String entitySet, Action a, RequestContext request, ResponseContext response) {
		Class<T> type = (Class<T>) classes.get(entitySet);
		Service<T> service = (Service<T>) services.get(type);
		T entity = request.getEntity(type);
		KeyMap key = request.getKeyMap();
		QueryInfo info = request.getContextObjectOfType(QueryInfo.class);
		switch (a) {
			case CREATE:
				return service.createEntity(type, request, response, entity);
			case DELETE:
				return service.deleteEntity(type, request, response, key);
			case GET:
				return service.getEntity(type, request, response, key);
			case LIST:
				return service.getEntities(type, request, response, key, info);
			case COUNT:
				return service.getEntitiesCount(type, request, response, key, info);
			case PATCH:
				return service.mergeEntity(type, request, response, entity, key);
			case UPDATE:
				return service.updateEntity(type, request, response, entity, key);

		}
		throw new IllegalOperationException("No Such action: " + a.name());
	}

	@Override
	public Class<?> getEntitySetClass(String entitySetName) {
		return classes.get(entitySetName);
	}

	@Override
	public Collection<Class<?>> getEntityTypes() {
		return classes.values();
	}

	@Override
	public Map<String, Class<?>> getKeysMap(String entitySetName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
	}

	@Override
	public boolean hasFunction(FunctionName name) {
		return functionServices.containsKey(name);
	}

	@Override
	public FunctionInfo getFunctionInfo(FunctionName functionName) {
		return functions.get(functionName);
	}

	@Override
	public Object invoke(FunctionName functionName, Map<String, Object> parameters, RequestContext request,
			ResponseContext response) {
		if (!hasFunction(functionName)) {
			throw new IllegalArgumentException("Function Not supported: " + functionName.getHttpMethod());
		}
		return functionServices.get(functionName).invoke(functionName, parameters, request, response);
	}

	@Override
	public Collection<FunctionName> getSupportedFunctions() {
		return functionServices.keySet();
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


	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public Service<?> getServiceForClass(Class<?> clazz) {
		return services.get(clazz);
	}

}
