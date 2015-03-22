package com.tj.producer.media;


public interface ReflectionResolver {
	Object getValue(Object subject, Object... args);

	void setValue(Object entity, Object value);

}
