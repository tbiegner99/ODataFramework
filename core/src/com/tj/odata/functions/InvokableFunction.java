package com.tj.odata.functions;

import java.util.Map;

import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;

public interface InvokableFunction {
	public Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response);
}
