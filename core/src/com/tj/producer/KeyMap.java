package com.tj.producer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;

import org.odata4j.core.NamedValue;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OEntityKey.KeyType;

import com.tj.producer.annotations.entity.Key;
import com.tj.producer.util.ReflectionUtil;

public class KeyMap implements Serializable {

	private static final long serialVersionUID = 8444957084871098427L;
	private static final String SINGLE_KEY = " ";
	private Map<String, Object> keys;
	private boolean singleKey;

	private KeyMap() {
		keys = new HashMap<String, Object>();
	}

	public Set<String> getComplexProperties() {
		return keys.keySet();
	}

	public Object getKey(String property) {
		return this.keys.get(property);
	}

	public Object getSingleKey() {
		if(keys.isEmpty()) {
			throw new IllegalArgumentException("No key available");
		}
		return this.keys.values().iterator().next();
	}

	public boolean isSingleKey() {
		return singleKey;
	}

	public <T> T setValuesToEntity(T entityTarget) {
		if(isSingleKey()) {
			String propName=getSingleKeyPropertyName(entityTarget);
			ReflectionUtil.setField(entityTarget, propName, getKey(SINGLE_KEY));
		} else {
			for(String s : getComplexProperties()) {
				ReflectionUtil.setField(entityTarget, s, getKey(s));
			}
		}
		return entityTarget;
	}

	public static String getSingleKeyPropertyName(Object entity) {
		for(Field f : entity.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Key.class) || f.isAnnotationPresent(Id.class)) {
				return f.getName();
			}
		}
		return null;
	}

	public static String getSingleKeyPropertyName(Class<?> entity) {
		for(Field f : entity.getDeclaredFields()) {
			if (f.isAnnotationPresent(Key.class) || f.isAnnotationPresent(Id.class)) {
				return f.getName();
			}
		}
		return null;
	}

	public static KeyMap fromOEntityKey(OEntityKey key) {
		KeyMap ret = new KeyMap();
		if (key.getKeyType() == KeyType.SINGLE) {
			ret.singleKey = true;
			ret.keys.put(SINGLE_KEY, key.asSingleValue());
		} else {
			for (NamedValue<?> v : key.asComplexValue()) {
				ret.keys.put(v.getName(), v.getValue());
			}
		}
		return ret;
	}
	public static KeyMap createFromSingleKey(Class<?> type, Object key) {
		KeyMap ret=new KeyMap();
		ret.keys.put(getSingleKeyPropertyName(type), key);
		return ret;
	}

	public static KeyMap build(Object entity, Class<?> type) {
		KeyMap ret = new KeyMap();
		buildKeys(entity, type);
		return ret;
	}

	private static void buildKeys(Object entity, Class<?> type) {
		for (Field f : type.getDeclaredFields()) {
			if (f.isAnnotationPresent(Key.class) || f.isAnnotationPresent(Id.class)) {
				if (!ReflectionUtil.isPrimitiveOrWrapper(f.getType())) {

				}
			}
		}
	}
}
