package com.tj.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tj.dao.filter.Filter;
import com.tj.odata.functions.FunctionInfo.FunctionName;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GenericSecurityManager implements CompositeSecurityManager {

	private Map<Class<?>, SecurityManager> entityTypeSecurity;
	private boolean allowedIfNoManager = true;
	private Map<FunctionName, FunctionSecurityManager> functionSecurity;

	public GenericSecurityManager() {
		entityTypeSecurity = new HashMap<Class<?>, SecurityManager>();
		functionSecurity = new HashMap<FunctionName, FunctionSecurityManager>();
	}

	public GenericSecurityManager(HashMap<Class<?>, SecurityManager> entitySecurity,
			HashMap<FunctionName, FunctionSecurityManager> functionSecurity) {
		entityTypeSecurity = entitySecurity;
		this.functionSecurity = functionSecurity;
	}

	public GenericSecurityManager(HashMap<Class<?>, SecurityManager> entitySecurity) {
		entityTypeSecurity = entitySecurity;
		functionSecurity = new HashMap<FunctionName, FunctionSecurityManager>();
	}

	@Override
	public <T> void addSecurityManagerForClass(Class<? extends T> clazz, SecurityManager<T, ?> manager) {
		entityTypeSecurity.put(clazz, manager);
	}

	@Override
	public <E> void addSecurityManagerForClasses(SecurityManager<E, ? extends User> manager,
			Class<? extends E>... clazz) {
		for (Class<?> c : clazz) {
			entityTypeSecurity.put(c, manager);
		}
	}

	@Override
	public <T> void addSecurityManagerForFunction(FunctionName function, FunctionSecurityManager<?> manager) {
		functionSecurity.put(function, manager);

	}

	public boolean isAllowedIfNoManager() {
		return allowedIfNoManager;
	}

	public void setAllowedIfNoManager(boolean allowIfNoManager) {
		this.allowedIfNoManager = allowIfNoManager;
	}

	@Override
	public <T> SecurityManager<T, ?> getSecurityManagerForClass(Class<T> clazz) {
		Class<?> clazz2 = clazz;
		if (entityTypeSecurity.containsKey(clazz2)) {
			return entityTypeSecurity.get(clazz2);
		}
		// try implemeted interfaces
		for (Class<?> inter : clazz.getInterfaces()) {
			if (entityTypeSecurity.containsKey(inter)) {
				return entityTypeSecurity.get(inter);
			}
		}
		// try superclasses
		while (clazz2 != Object.class && !entityTypeSecurity.containsKey(clazz2)) {
			clazz2 = clazz2.getSuperclass();
		}
		return entityTypeSecurity.get(clazz2);
	}

	@Override
	public FunctionSecurityManager<?> getSecurityManagerForFunction(FunctionName functionName) {
		if (functionSecurity.containsKey(functionName)) {
			return functionSecurity.get(functionName);
		}
		functionName = FunctionName.ALL_OF_NAME(functionName.getName());
		if (functionSecurity.containsKey(functionName)) {
			return functionSecurity.get(functionName);
		}
		functionName = FunctionName.ALL_OF_METHOD(functionName.getHttpMethod());
		if (functionSecurity.containsKey(functionName)) {
			return functionSecurity.get(functionName);
		}
		return functionSecurity.get(FunctionName.ALL);
	}

	@Override
	public boolean canReadEntity(Class<? extends Object> type, User u) {
		SecurityManager manager = getSecurityManagerForClass(type);
		if (manager == null) {
			return allowedIfNoManager;
		}
		return manager.canReadEntity(type, u);
	}

	@Override
	public boolean canWriteEntity(Object entity, User u) {
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return allowedIfNoManager;
		}
		return ((SecurityManager<Object, User>) getSecurityManagerForClass(entity.getClass()))
				.canWriteEntity(entity, u);
	}

	@Override
	public boolean canUpdateEntity(Object entity, User u) {
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return allowedIfNoManager;
		}
		return ((SecurityManager<Object, User>) getSecurityManagerForClass(entity.getClass())).canUpdateEntity(entity,
				u);
	}

	@Override
	public boolean canDeleteEntity(Class<? extends Object> type, User u) {
		SecurityManager manager = getSecurityManagerForClass(type);
		if (manager == null) {
			return allowedIfNoManager;
		}
		return ((SecurityManager<Object, User>) getSecurityManagerForClass(type)).canDeleteEntity(type, u);
	}

	@Override
	public boolean canAccessProperty(Object entity, String property, User u) {
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return allowedIfNoManager;
		}
		return ((SecurityManager<Object, User>) getSecurityManagerForClass(entity.getClass())).canAccessProperty(
				entity, property, u);
	}

	@Override
	public boolean canReadProperty(Object entity, String property, User u) {
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return allowedIfNoManager;
		}
		return ((SecurityManager<Object, User>) getSecurityManagerForClass(entity.getClass())).canReadProperty(entity,
				property, u);
	}

	@Override
	public boolean canWriteProperty(Object entity, String property, User u) {
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return allowedIfNoManager;
		}
		return ((SecurityManager<Object, User>) getSecurityManagerForClass(entity.getClass())).canWriteProperty(entity,
				property, u);
	}

	@Override
	public boolean canUpdateProperty(Object entity, String property, User u) {
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return allowedIfNoManager;
		}
		return ((SecurityManager<Object, User>) getSecurityManagerForClass(entity.getClass())).canUpdateProperty(
				entity, property, u);
	}

	@Override
	public Object getPropertyValueForCreate(Object entity, Object supplied, String property, User u) {
		if (!canWriteProperty(entity, property, u)) {
			throw new IllegalAccessError("User does not have permission to create an object with the property: "
					+ property);
		}
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return supplied;
		}
		return manager.getPropertyValueForCreate(entity, supplied, property, u);
	}

	@Override
	public Object getPropertyValueForUpdate(Object entity, Object supplied, String property, User u) {
		if (!canUpdateProperty(entity, property, u)) {
			throw new IllegalAccessError("User does not have permission to update the property: " + property);
		}
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return supplied;
		}
		return manager.getPropertyValueForUpdate(entity, supplied, property, u);
	}

	@Override
	public Object getPropertyValueForRead(Object entity, Object supplied, String property, User u) {
		if (!canReadProperty(entity, property, u)) {
			throw new IllegalAccessError("User does not have permission to read the property: " + property);
		}
		SecurityManager manager = getSecurityManagerForClass(entity.getClass());
		if (manager == null) {
			return supplied;
		}
		return manager.getPropertyValueForRead(entity, supplied, property, u);
	}

	@Override
	public Collection<Filter> getUserLevelFilters(Class<Object> clazz, User u) {
		SecurityManager manager = getSecurityManagerForClass(clazz);
		if (manager == null) {
			return new ArrayList<Filter>();
		}
		return manager.getUserLevelFilters(clazz, u);
	}

	@Override
	public Map<String, Object> getFilterParameters(Class<Object> clazz, User u) {
		SecurityManager manager = getSecurityManagerForClass(clazz);
		if (manager == null) {
			return new HashMap<String, Object>();
		}
		return manager.getFilterParameters(clazz, u);
	}

	public Map<Class<?>, SecurityManager> getEntityTypeSecurity() {
		return entityTypeSecurity;
	}

	public void setEntityTypeSecurity(Map<Class<?>, SecurityManager> entityTypeSecurity) {
		this.entityTypeSecurity = entityTypeSecurity;
	}

	public Map<FunctionName, FunctionSecurityManager> getFunctionSecurity() {
		return functionSecurity;
	}

	public void setFunctionSecurity(Map<FunctionName, FunctionSecurityManager> functionSecurity) {
		this.functionSecurity = functionSecurity;
	}

}
