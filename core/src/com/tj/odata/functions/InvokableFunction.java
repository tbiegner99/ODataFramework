package com.tj.odata.functions;

import java.util.Map;

import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.configuration.ProducerConfiguration;

/***
 * Represents an object that can have an action performed on it with suppluied parameters and contexts.
 * @author tbiegner
 *
 */
public interface InvokableFunction {
	/***
	 * Performs some action defined by the implementor
	 * @param name  - how the fuction is called. This is the name and httop method that was called. Due to possible aliasing, this is important.
	 * @param parameters - Parameters for which to execute the function
	 * @param request - information relating to http request for the function call, if applicable
	 * @param response - a placeholder for response metadata
	 * @param config - the application context. contains all services and functions for the application and security context
	 * @return some object for the action or null
	 */
	public Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response,ProducerConfiguration config);
}
