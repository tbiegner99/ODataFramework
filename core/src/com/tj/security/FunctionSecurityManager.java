package com.tj.security;

public interface FunctionSecurityManager<E extends User> {

	boolean canCallFunction(E user);

	Object getReturnValue(Object retValue, E user);

	Object getFunctionArgument(Object argument, E user);
}
