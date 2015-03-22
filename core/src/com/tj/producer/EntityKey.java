package com.tj.producer;

import java.util.HashMap;

public class EntityKey extends HashMap<String, Object>{
	public static final String SIMPLE_KEY = " ";

	public Object getSimpleKey() {
		if (!containsKey(" ")) {
			throw new RuntimeException();
		}
		return get(SIMPLE_KEY);
	}
}
