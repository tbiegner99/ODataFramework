package com.tj.odata.functions;

/***
 * Adds alias information to an invokation, which allows functions to map
 * how they should be called.
 * 
 * @author Admin
 *
 */
public interface Function extends InvokableFunction {
	public FunctionInfo getAliases();
}
