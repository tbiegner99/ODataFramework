package com.tj.odata.functions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpMethod;

/***
 * Contains meta information about a function that can be invoked using an OData url. This is used to generate metadata
 * document.
 * 
 * @author Admin
 *
 */
public class FunctionInfo {
	private Map<String, Class<?>> parameters;
	private Set<String> requiredParameters;
	private Class<?> returnType;
	private boolean collectionReturned;
	private FunctionName name;

	public FunctionInfo(FunctionName name, Map<String, Class<?>> parameters, Set<String> requiredParameters,
			Class<?> returnType, boolean returnsCollection) {
		this.parameters = parameters;
		this.requiredParameters = requiredParameters;
		this.returnType = returnType;
		this.name = name;
		this.collectionReturned = returnsCollection;
	}

	public FunctionName getName() {
		return name;
	}

	public Map<String, Class<?>> getParameters() {
		return parameters;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public static Builder newBuilder(String name) {
		return new Builder(name);
	}

	public boolean isCollectionReturned() {
		return collectionReturned;
	}

	public boolean isParameterRequired(String paramName) {
		return requiredParameters.contains(paramName);
	}

	public static class Builder {
		private Map<String, Class<?>> parameters;
		private String name;
		private Class<?> returnType;
		private Set<String> requiredParameters;
		private boolean returnsCollection = false;
		private String httpMethod = "GET";

		private Builder(String name) {
			parameters = new HashMap<String, Class<?>>();
			requiredParameters = new HashSet<String>();
			this.name = name;
		}

		public Builder addParameter(String name, Class<?> type) {
			this.parameters.put(name, type);
			return this;
		}

		public Builder addRequiredParameter(String name, Class<?> type) {
			this.parameters.put(name, type);
			this.requiredParameters.add(name);
			return this;
		}

		public Builder setCollectionReturnType(Class<?> type) {
			this.returnType = type;
			this.returnsCollection = true;
			return this;
		}

		public Builder setSimpleReturnType(Class<?> type) {
			this.returnType = type;
			this.returnsCollection = false;
			return this;
		}

		public Builder setHttpMethod(String httpMethod) {
			this.httpMethod = httpMethod;
			return this;
		}

		public Builder setHttpMethod(HttpMethod httpMethod) {
			return setHttpMethod(httpMethod.name());
		}

		public FunctionInfo build() {
			return new FunctionInfo(new FunctionName(name, httpMethod), parameters, requiredParameters, returnType,
					returnsCollection);
		}
	}

	public static class FunctionName {
		public static FunctionName ALL = new FunctionName("*", "*");

		public static FunctionName ALL_OF_NAME(String name2) {
			return new FunctionName(name2, "*");
		}

		public static FunctionName ALL_OF_METHOD(String method) {
			return new FunctionName("*", method);
		}

		private String name;
		private String httpMethod;

		public FunctionName(String name, String httpMethod) {
			this.name = name;
			this.httpMethod = httpMethod;
		}

		public String getName() {
			return name;
		}

		public String getHttpMethod() {
			return httpMethod;
		}

		public int hashCode() {
			return (name + "_" + httpMethod).hashCode();

		}

		public boolean equals(Object info) {
			if (info == null || !FunctionName.class.isAssignableFrom(info.getClass())) {
				return false;
			}
			FunctionName cmp = (FunctionName) info;
			return ((name == null ? cmp.name == null : name.equals(cmp.name)) && (httpMethod == null ? cmp.httpMethod == null
					: httpMethod.equals(cmp.httpMethod)));
		}

	}

}
