package com.tj.security;

import com.tj.security.user.User;

public interface FunctionSecurityManager<E extends User> {

	boolean canCallFunction(E user);

	Object getReturnValue(Object retValue, E user);

	Object getFunctionArgument(Object argument, E user);
}
