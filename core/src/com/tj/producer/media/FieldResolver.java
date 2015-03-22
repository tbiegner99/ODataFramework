package com.tj.producer.media;

import java.lang.reflect.Field;

public class FieldResolver implements ReflectionResolver {
	private Field field;

	public FieldResolver(Field f) {
		field = f;
	}

	@Override
	public Object getValue(Object subject, Object... args) {
		field.setAccessible(true);
		try {
			return field.get(subject);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
	}

	@Override
	public void setValue(Object entity, Object value) {
		if (!field.getType().isAssignableFrom(value.getClass())) {
			throw new RuntimeException();
		}
		field.setAccessible(true);
		try {
			field.set(entity, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException();
		}
	}
}
