package com.tj.producer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.odata4j.core.OEntityKey;
import org.odata4j.producer.ODataContext;
import org.odata4j.producer.ODataHeadersContext;
import org.odata4j.producer.QueryInfo;

import com.tj.security.CompositeSecurityManager;
import com.tj.security.SecurityManager;
import com.tj.security.user.User;

public class RequestContext {
	private Map<String, String> headers;
	private Object entity;
	private KeyMap keys;

	private Map<Class<?>, Object> context;
	private HttpServletRequest requestContext;
	private CompositeSecurityManager securityManager;
	private User user;

	public RequestContext(Map<String, String> headers, Collection<Object> objects, Object entity2, KeyMap map,
			HttpServletRequest request, CompositeSecurityManager manager, User user) {
		this.headers = headers;
		this.context = new HashMap<Class<?>, Object>();
		this.entity = entity2;
		this.keys = map;
		this.user = user;
		this.securityManager = manager;
		for (Object o : objects) {
			context.put(o.getClass(), o);
		}
		context.put(SecurityManager.class, manager);
		context.put(User.class, user);
		this.requestContext = request;
	}

	public RequestContext(Map<String, String> headers, Collection<Object> objects, HttpServletRequest request,
			CompositeSecurityManager manager, User user) {
		this(headers, objects, null, null, request, manager, user);
	}
	public QueryInfo getQueryInfo() {
		return getContextObjectOfType(QueryInfo.class);
	}
	public String getRawHeader(String name) {
		if (name == null) {
			return null;
		}
		return headers.get(name.toLowerCase());
	}

	public <T> Object getHeaderOfType(String header, Class<T> type) {
		if (!headers.containsKey(header)) {
			return null;
		}
		String headerVal = headers.get(header);
		if (type == String.class) {
			return headerVal;
		}
		if (type == Integer.class) {
			return Integer.parseInt(headerVal);
		}
		if (type == Double.class) {
			return Double.parseDouble(headerVal);
		}
		if (type == Float.class) {
			return Float.parseFloat(headerVal);
		}
		if (type == Character.class) {
			return headerVal.charAt(0);
		}
		if (type == Long.class) {
			return Long.parseLong(headerVal);
		}
		if (type == Short.class) {
			return Short.parseShort(headerVal);
		}
		if (type == Byte.class) {
			return Byte.parseByte(headerVal);
		}
		throw new RuntimeException();
	}

	public KeyMap getKeyMap() {
		return keys;
	}

	public <T> T getEntity(Class<T> type) {
		return type.cast(entity);
	}

	public <T> T getContextObjectOfType(Class<T> type) {
		if (!context.containsKey(type)) {
			return null;
		}
		return type.cast(context.get(type));
	}

	public Object getSessionValue(String key) {
		if (requestContext == null || requestContext.getSession() == null) {
			return null;
		}
		return requestContext.getSession().getAttribute(key);
	}

	public Map<String, Object> getSessionValues() {
		if (requestContext == null || requestContext.getSession() == null) {
			return null;
		}
		Enumeration<?> attr = requestContext.getSession().getAttributeNames();
		Map<String, Object> ret = new HashMap<String, Object>();
		while (attr.hasMoreElements()) {
			String name = attr.nextElement().toString();
			ret.put(name, requestContext.getSession().getAttribute(name));
		}
		return ret;
	}

	public CompositeSecurityManager getSecurityManager() {
		return securityManager;
	}

	public <T> SecurityManager<T, ? extends User> getSecurityManager(Class<T> type) {
		return securityManager.getSecurityManagerForClass(type);
	}

	public User getUser() {
		return user;
	}
	public static RequestContext createRequestContext(ODataContext ocontext,OEntityKey key, Object entity, Class<?> type,
					HttpServletRequest requestContext, CompositeSecurityManager manager, User user) {
				List<Object> objects = new ArrayList<Object>();
				objects.add(entity);
				KeyMap map = KeyMap.fromOEntityKey(key);
				return new RequestContext(null, objects, entity, map, requestContext, manager, user);
	}
	public static RequestContext createRequestContext(ODataContext ocontext, Object entity, Class<?> type,
			HttpServletRequest requestContext, CompositeSecurityManager manager, User user) {
		List<Object> objects = new ArrayList<Object>();
		objects.add(entity);
		KeyMap map = KeyMap.build(entity, type);
		return new RequestContext(null, objects, entity, map, requestContext, manager, user);
	}

	public static RequestContext createRequestContext(ODataContext ocontext, QueryInfo info, Class<?> type,
			HttpServletRequest requestContext, CompositeSecurityManager manager, User user) {
		List<Object> objects = new ArrayList<Object>();
		objects.add(info);
		return new RequestContext(mapFromODataContext(ocontext), objects, requestContext, manager, user);
	}

	public static RequestContext createRequestContext(ODataContext ocontext, OEntityKey key, Class<?> type,
			HttpServletRequest requestContext, CompositeSecurityManager manager, User user) {
		List<Object> objects = new ArrayList<Object>();
		KeyMap map = KeyMap.fromOEntityKey(key);
		return new RequestContext(mapFromODataContext(ocontext), objects, null, map, requestContext, manager, user);
	}

	public static RequestContext createRequestContext(ODataContext ocontext, OEntityKey key, QueryInfo info,
			Class<?> type, HttpServletRequest requestContext, CompositeSecurityManager manager, User user) {
		List<Object> objects = new ArrayList<Object>();
		objects.add(info);
		KeyMap map = KeyMap.fromOEntityKey(key);
		return new RequestContext(mapFromODataContext(ocontext), objects, null, map, requestContext, manager, user);
	}
	public static RequestContext createRequestContext(QueryInfo info,Class<?> type, CompositeSecurityManager manager, User user) {
		return RequestContext.createRequestContext(null,info,type,null,manager,user);
	}
	private static Map<String, String> mapFromODataContext(ODataContext context) {
		Map<String, String> ret = new HashMap<String, String>();
		if(context==null) {
			return ret;
		}
		ODataHeadersContext headers = context.getRequestHeadersContext();
		if (headers != null) {
			for (String s : headers.getRequestHeaderFieldNames()) {
				ret.put(s, headers.getRequestHeaderValue(s));
			}
		}
		return ret;
	}

}
