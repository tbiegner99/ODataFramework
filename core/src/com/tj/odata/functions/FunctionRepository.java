package com.tj.odata.functions;

import java.util.Collection;

import com.tj.odata.functions.FunctionInfo.FunctionName;

public interface FunctionRepository {
	public Collection<FunctionName> getSupportedFunctions();

	public boolean hasFunction(FunctionName name);

	public FunctionInfo getFunctionInfo(FunctionName functionName);
}
