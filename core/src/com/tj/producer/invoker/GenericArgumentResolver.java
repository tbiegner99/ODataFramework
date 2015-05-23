package com.tj.producer.invoker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class GenericArgumentResolver {
	private Map<Class<?>, Queue<Object>> args = new HashMap<Class<?>, Queue<Object>>();

	public GenericArgumentResolver(Object[] candidates) {
		for (Object o : candidates) {
			if (!args.containsKey(o.getClass())) {
				LinkedList<Object> list = new LinkedList<Object>();
				args.put(o.getClass(), list);
			}
			args.get(o.getClass()).add(o);
		}
	}

	public Object[] getArguments(Class<?>[] parameters) {
		return getArguments(Arrays.asList(parameters));
	}

	public Object[] getArguments(List<Class<?>> parameters) {
		Object[] args = new Object[parameters.size()];
		int i = 0;
		for (Class<?> type : parameters) {
			args[i] = null;
			Class<?> sup = type;
			while (sup != null) {
				if (this.args.containsKey(sup)) {
					args[i] = this.args.get(sup).poll();
					break;
				}
				sup = sup.getSuperclass();
			}
			i++;
		}
		return args;
	}
}
