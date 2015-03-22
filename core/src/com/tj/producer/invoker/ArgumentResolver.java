package com.tj.producer.invoker;

import com.tj.producer.RequestContext;

public interface ArgumentResolver {
	Object resolveArgument(RequestContext context);
}
