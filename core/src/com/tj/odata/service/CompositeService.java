package com.tj.odata.service;

import java.util.Collection;

public interface CompositeService extends Service<Object> {
	Collection<Class<?>> getTypes();
}
