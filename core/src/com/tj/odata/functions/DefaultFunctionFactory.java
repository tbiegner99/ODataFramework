package com.tj.odata.functions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.tj.odata.functions.FunctionInfo.FunctionName;

public class DefaultFunctionFactory<T extends Function> implements FunctionFactory<T> {
	private Map<FunctionName, T> functions;

	public DefaultFunctionFactory(Collection<T> functions) {
		this();
		for (T func : functions) {
			this.functions.put(func.getAliases().getName(), func);
		}
	}

	protected DefaultFunctionFactory() {
		this.functions = new HashMap<FunctionName, T>();
	}

	protected Collection<T> getFunctions() {
		return functions.values();
	}

	protected Collection<T> getItems() {
		return functions.values();
	}

	protected void addItem(T f) {
		functions.put(f.getAliases().getName(), f);
	}

	@Override
	public T getFunction(FunctionName name) {
		if (!hasFunction(name)) {
			throw new IllegalArgumentException("");
		}
		return functions.get(name);
	}

	@Override
	public Collection<FunctionName> getSupportedFunctions() {
		return functions.keySet();
	}

	@Override
	public boolean hasFunction(FunctionName name) {
		return functions.containsKey(name);
	}

	@Override
	public FunctionInfo getFunctionInfo(FunctionName functionName) {
		return getFunction(functionName).getAliases();
	}

}
