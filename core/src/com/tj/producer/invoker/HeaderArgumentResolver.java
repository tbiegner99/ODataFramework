package com.tj.producer.invoker;

import com.tj.producer.RequestContext;

public class HeaderArgumentResolver implements ArgumentResolver {
	private String headerName;
	private Class<?> argType;

	public HeaderArgumentResolver(String header, Class<?> type) {
		headerName = header;
		argType = type;
	}

	@Override
	public Object resolveArgument(RequestContext context) {
		return context.getHeaderOfType(headerName, argType);
	}

}
