package com.tj.producer;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.odata4j.core.NamedValue;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OEntityKey.KeyType;

import com.tj.producer.annotations.entity.Key;

public class KeyMap implements Serializable {

	private static final long serialVersionUID = 8444957084871098427L;
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
		return this.keys.values().iterator().next();
	}

	public boolean isSingleKey() {
		return singleKey;
	}

	public static KeyMap fromOEntityKey(OEntityKey key) {
		KeyMap ret = new KeyMap();
		if (key.getKeyType() == KeyType.SINGLE) {
			ret.singleKey = true;
			ret.keys.put("single", key.asSingleValue());
		} else {
			for (NamedValue<?> v : key.asComplexValue()) {
				ret.keys.put(v.getName(), v.getValue());
			}
		}
		return ret;
	}

	public static KeyMap build(Object entity, Class<?> type) {
		KeyMap ret = new KeyMap();
		buildKeys(entity, type);
		return ret;
	}

	private static void buildKeys(Object entity, Class<?> type) {
		for (Field f : type.getDeclaredFields()) {
			if (f.isAnnotationPresent(Key.class)) {
				if (f.getType().isPrimitive()) {

				}
			}
		}
	}
}
