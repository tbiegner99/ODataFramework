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
	private boolean skipToken;

	/***
	 * Creates a functionInfo obect. Using {@link FunctionInfo.Builder} is highly recommended.
	 * 
	 * @param name
	 * @param parameters
	 * @param requiredParameters
	 * @param returnType
	 * @param returnsCollection
	 * @param skipToken
	 */
	public FunctionInfo(FunctionName name, Map<String, Class<?>> parameters, Set<String> requiredParameters,
			Class<?> returnType, boolean returnsCollection, boolean skipToken) {
		this.parameters = parameters;
		this.requiredParameters = requiredParameters;
		this.returnType = returnType;
		this.name = name;
		this.skipToken = skipToken;
		this.collectionReturned = returnsCollection;
	}

	/**
	 * @return true if the function should include a skip token in the return object.
	 *         Applicable for collection return types.
	 */
	public boolean includeSkipToken() {
		return skipToken;
	}

	/***
	 * 
	 * @return calling information of the function (name/http verb)
	 */
	public FunctionName getName() {
		return name;
	}

	/**
	 * 
	 * @return a set of parameters expected by the function as well as the expected type
	 */
	public Map<String, Class<?>> getParameters() {
		return parameters;
	}

	/**
	 * 
	 * @return Java type returned by the function
	 */
	public Class<?> getReturnType() {
		return returnType;
	}

	/***
	 * Creates a builder for a function info object.
	 * 
	 * @param name
	 *            - the name of the function to create a builder for. The combination of name/http method must be unique
	 * @return
	 */
	public static Builder newBuilder(String name) {
		return new Builder(name);
	}

	/***
	 * Returns true if the return type is a collection
	 * 
	 * @return
	 */
	public boolean isCollectionReturned() {
		return collectionReturned;
	}

	/***
	 * Returns true if a parameter is required
	 * 
	 * @param paramName
	 *            - name of the parameter
	 * @return
	 */
	public boolean isParameterRequired(String paramName) {
		return requiredParameters.contains(paramName);
	}

	/***
	 * Assists in building the immutable FunctionInfo object. All setters return this, so they can be chained.
	 * 
	 * @author tbiegner
	 *
	 */
	public static class Builder {
		private Map<String, Class<?>> parameters;
		private String name;
		private Class<?> returnType;
		private Set<String> requiredParameters;
		private boolean returnsCollection = false;
		private boolean skipToken = false;
		private String httpMethod = "GET";

		private Builder(String name) {
			parameters = new HashMap<String, Class<?>>();
			requiredParameters = new HashSet<String>();
			this.name = name;
		}

		/***
		 * Defines an acceptable parameter for a function
		 * 
		 * @param name
		 *            - the parameter name
		 * @param type
		 *            - the type of the parameter. Recommended to be primitive or wrapper type. Other types may have
		 *            unpredictable results.
		 * @return
		 */
		public Builder addParameter(String name, Class<?> type) {
			this.parameters.put(name, type);
			return this;
		}

		/***
		 * Defines a required parameter for a function. An exception will be thrown on the call if this parameter is not
		 * supplied.
		 * 
		 * @param name
		 *            - the parameter name
		 * @param type
		 *            - the type of the parameter. Recommended to be primitive or wrapper type. Other types may have
		 *            unpredictable results.
		 * @return
		 */
		public Builder addRequiredParameter(String name, Class<?> type) {
			this.parameters.put(name, type);
			this.requiredParameters.add(name);
			return this;
		}

		/**
		 * Sets the return type as a collection.
		 * NOTE: only one return type may be applied. This also applies for setSimpleReturnType.
		 * 
		 * @param type
		 *            - the type of the returned collection
		 * @return
		 */
		public Builder setCollectionReturnType(Class<?> type) {
			this.returnType = type;
			this.returnsCollection = true;
			return this;
		}

		/**
		 * Sets the return type as a basic java type. This may or may not be an entity
		 * NOTE: only one return type may be applied. This also applies for setSimpleReturnType.
		 * 
		 * @param type
		 *            - the type of the returned collection
		 * @return
		 */
		public Builder setSimpleReturnType(Class<?> type) {
			this.returnType = type;
			this.returnsCollection = false;
			return this;
		}

		/***
		 * Sets the http verb to a non standard string. due to limitations in jersey and certain web servers,
		 * this may not be supported
		 * If none is supplied, defaults to GET
		 * 
		 * @param httpMethod
		 * @return
		 */
		public Builder setHttpMethod(String httpMethod) {
			this.httpMethod = httpMethod;
			return this;
		}

		/**
		 * Sets the function method to a standasrd http verb.
		 * If none is supplied, defaults to GET
		 * 
		 * @param httpMethod
		 * @return
		 */
		public Builder setHttpMethod(HttpMethod httpMethod) {
			return setHttpMethod(httpMethod.name());
		}

		/***
		 * Recommended only for colection return types. Whether to include a skip token in the response. this is for
		 * server side paging.
		 * 
		 * @param skipToken
		 * @return the current builder instance
		 */
		public Builder includeSkipToken(boolean skipToken) {
			this.skipToken = skipToken;
			return this;
		}

		/***
		 * Creates a functionInfo obect from the supplied prameters
		 * 
		 * @return
		 */
		public FunctionInfo build() {
			return new FunctionInfo(new FunctionName(name, httpMethod), parameters, requiredParameters, returnType,
					returnsCollection, skipToken);
		}
	}

	/***
	 * Defines a function header. The name/http methoid combination must be unique
	 * 
	 * @author tbiegner
	 *
	 */
	public static class FunctionName {
		/**
		 * Representation of a call to any function.
		 */
		public static FunctionName ALL = new FunctionName("*", "*");

		/**
		 * Creates a reprentation of a a call to a function with a given name for any http method
		 * 
		 * @param name2
		 * @return
		 */
		public static FunctionName ALL_OF_NAME(String name2) {
			return new FunctionName(name2, "*");
		}

		/**
		 * Creates a reprentation of a a call to a function to a given http method for any function name
		 * 
		 * @param name2
		 * @return
		 */
		public static FunctionName ALL_OF_METHOD(String method) {
			return new FunctionName("*", method);
		}

		private String name;
		private String httpMethod;

		public FunctionName(String name, String httpMethod) {
			this.name = name;
			this.httpMethod = httpMethod;
		}

		/***
		 * @return name of the function call or * for any
		 */
		public String getName() {
			return name;
		}

		/***
		 * @return name of the http verb for the function call or * for any
		 */
		public String getHttpMethod() {
			return httpMethod;
		}

		/**
		 * important so that different object may map to the same thing in a map
		 */
		@Override
		public int hashCode() {
			return (name + "_" + httpMethod).hashCode();
		}

		/***
		 * Important so that differnet instances may be identified as representing the same function
		 */
		@Override
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
