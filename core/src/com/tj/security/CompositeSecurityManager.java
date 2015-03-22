package com.tj.security;

import com.tj.odata.functions.FunctionInfo.FunctionName;

public interface CompositeSecurityManager extends SecurityManager<Object, User> {

	<T> SecurityManager<T, ?> getSecurityManagerForClass(Class<T> clazz);

	FunctionSecurityManager<?> getSecurityManagerForFunction(FunctionName functionName);

	<T> void addSecurityManagerForClass(Class<? extends T> clazz, SecurityManager<T, ?> manager);

	<T> void addSecurityManagerForClasses(SecurityManager<T, ?> manager,
			@SuppressWarnings("unchecked") Class<? extends T>... clazz);

	<T> void addSecurityManagerForFunction(FunctionName function, FunctionSecurityManager<?> manager);
}
