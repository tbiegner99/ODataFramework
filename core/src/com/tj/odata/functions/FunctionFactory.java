package com.tj.odata.functions;

import com.tj.odata.functions.FunctionInfo.FunctionName;

public interface FunctionFactory<T extends Function> extends FunctionRepository {
	public T getFunction(FunctionName name);

}
