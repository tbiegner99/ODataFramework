package com.tj.producer.media;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.hibernate.cfg.NotYetImplementedException;

public class MethodResolver implements ReflectionResolver {
	private Method method;

	public MethodResolver(Method f) {
		method = f;
	}

	@Override
	public Object getValue(Object subject, Object... args) {
		if (!Modifier.isPublic(method.getModifiers())) {
			return null;
		}
		try {
			return method.invoke(subject, args);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
	}

	@Override
	public void setValue(Object entity, Object value) {
		throw new NotYetImplementedException("No set method");

	}

}
