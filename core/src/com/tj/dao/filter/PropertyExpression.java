package com.tj.dao.filter;

public class PropertyExpression implements Expression {

	private String property;

	public PropertyExpression(String propertyName) {
		this.property = propertyName;// .replace("/", ".");
	}

	public String getProperty() {
		return property;
	}

}
