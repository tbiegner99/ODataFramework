package com.tj.producer.invoker;

import com.tj.producer.RequestContext;

public class RequestContextResolver implements ArgumentResolver {
	private Class<?> objectType;

	public RequestContextResolver(Class<?> type) {
		objectType = type;
	}

	@Override
	public Object resolveArgument(RequestContext context) {
		return context.getContextObjectOfType(objectType);
	}

}
