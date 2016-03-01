package com.tj.odata.functions;

import java.util.Collection;

import com.tj.odata.functions.FunctionInfo.FunctionName;

/***
 * An object for storing a subset of functions in the application.
 * Note: it is not responsible for routing function calls
 * 
 * @see {@link FunctionService} {@link FunctionRepository}
 * @author Admin
 *
 */
public interface FunctionRepository {
	public Collection<FunctionName> getSupportedFunctions();

	public boolean hasFunction(FunctionName name);

	public FunctionInfo getFunctionInfo(FunctionName functionName);
}
