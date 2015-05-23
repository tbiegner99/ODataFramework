package com.tj.producer.media;
/**
 * Static Resolver is a way to set a constant media content type
 * or binary. It is not based on fields so setValue is not relevant
 * @author tbiegner
 *
 */
public class StaticResolver implements ReflectionResolver {
	private Object value;

	StaticResolver(Object value) {
		this.value = value;
	}

	@Override
	public Object getValue(Object subject, Object... args) {
		return value;
	}

	@Override
	public void setValue(Object entity, Object value) {
		// throw illegal operation exception
	}

}
