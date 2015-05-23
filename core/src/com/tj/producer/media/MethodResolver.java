package com.tj.producer.media;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.hibernate.cfg.NotYetImplementedException;

import com.tj.producer.invoker.GenericArgumentResolver;

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
			GenericArgumentResolver resolver = new GenericArgumentResolver(args);
			return method.invoke(subject, resolver.getArguments(method.getParameterTypes()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			return null;
		}
	}

	@Override
	public void setValue(Object entity, Object value) {
		throw new NotYetImplementedException("No set method");

	}

}
