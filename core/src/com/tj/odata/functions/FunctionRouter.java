package com.tj.odata.functions;

import java.util.Collection;
import java.util.Map;

import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.configuration.ProducerConfiguration;

public class FunctionRouter<T extends Function> implements FunctionService {
	private FunctionFactory<T> factory;

	public FunctionRouter(FunctionFactory<T> factory) {
		this.factory = factory;
	}

	public FunctionRouter(Collection<T> functions) {
		this(new DefaultFunctionFactory<T>(functions));
	}

	@Override
	public Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response,ProducerConfiguration config) {
		return factory.getFunction(name).invoke(name, parameters, request, response,config);
	}

	@Override
	public FunctionInfo getFunctionInfo(FunctionName functionName) {
		if (!factory.hasFunction(functionName)) {
			throw new IllegalArgumentException("No Such Function: " + functionName.getName());
		}
		return factory.getFunction(functionName).getAliases();
	}

	@Override
	public Collection<FunctionName> getSupportedFunctions() {
		return factory.getSupportedFunctions();
	}

	@Override
	public boolean hasFunction(FunctionName name) {
		return factory.hasFunction(name);
	}

	protected FunctionFactory<T> getFactory() {
		return factory;
	}

}
