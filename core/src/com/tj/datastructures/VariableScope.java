package com.tj.datastructures;

import java.util.HashMap;
import java.util.Map;

/***
 * This is an object for maintaining variables that are scoped.
 * In particular any and all odata queries can declare variables,
 * and this object helps manage those declarations.
 * 
 * @author Admin
 *
 * @param <T>
 */

public class VariableScope<T> {
	private Map<String, T> scope;

	public VariableScope(T defaultScope) {
		scope = new HashMap<String, T>();
		addVariable("", defaultScope);
	}

	private VariableScope(VariableScope<T> scope) {
		this.scope = new HashMap<String, T>(scope.scope);
	}

	public VariableScope<T> createSubScope() {
		return new VariableScope<T>(this);
	}

	public void addVariable(String name, T value) {
		scope.put(name, value);
	}

	public T getDefaultScopeVariable() {
		return scope.get("");
	}

	public T getVariable(String variable) {
		return scope.get(variable);
	}

	public boolean isVariableInScope(String variable) {
		return scope.containsKey(variable);
	}
}
