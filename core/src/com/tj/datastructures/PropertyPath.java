package com.tj.datastructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.odata4j.expression.EntitySimpleProperty;

/***
 * A recursive structure (trie) for storing all paths. This is used to store for instance all $select or $expand paths
 *
 * @author Admin
 *
 */
public class PropertyPath {
	private Map<String, PropertyPath> tree;
	private String pathComponent;

	public PropertyPath(Collection<String> strings) {
		this((String) null);
		for (String s : strings) {
			addPath(s);
		}
	}

	public PropertyPath(List<EntitySimpleProperty> strings) {
		this((String) null);
		for (EntitySimpleProperty s : strings) {
			if (s == null) {
				continue;
			}
			addPath(s.getPropertyName());
		}
	}

	private PropertyPath(String name) {
		tree = new HashMap<String, PropertyPath>();
		this.pathComponent = name;
	}

	public static PropertyPath getEmptyPropertyPath() {
		return new PropertyPath((String) null);
	}

	public String getPathComponent() {
		return pathComponent;
	}

	public boolean isLeaf() {
		return tree.isEmpty();
	}

	public PropertyPath getSubPath(String path) {
		if (path == null || path.isEmpty() || this.isLeaf()) {
			return this;
		}
		if (path.contains("/")) {
			String[] components = path.split("/");
			PropertyPath ret = this;
			for (String component : components) {
				ret = tree.get(component);
				if (ret == null) {
					return null;
				}
			}
			return ret;
		}
		return tree.get(path);
	}

	private void addPath(String s) {
		addPath(s.split("/"), 0);
	}

	protected void addPath(String[] components, int i) {
		if (i >= components.length) {
			return;
		}
		if (!tree.containsKey(components[i])) {
			PropertyPath p = new PropertyPath(components[i]);
			tree.put(components[i], p);
			p.addPath(components, i + 1);
		} else {
			tree.get(components[i]).addPath(components, i + 1);
		}
	}

	public boolean nextComponentContains(String name) {
		return tree.containsKey(name);
	}

}
