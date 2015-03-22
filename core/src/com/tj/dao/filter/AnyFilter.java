package com.tj.dao.filter;

import org.odata4j.expression.AggregateAnyFunction;

public class AnyFilter implements Filter {
	private Filter predicate;
	private Expression source;
	private String variable;

	public AnyFilter(Expression source, String variable, Filter predicate) {
		this.source = source;
		this.predicate = predicate;
		this.variable = variable;
	}

	public String getVariable() {
		return variable;
	}

	public Filter getPredicate() {
		return predicate;
	}

	public Expression getSource() {
		return source;
	}

	public static AnyFilter fromFilter(AggregateAnyFunction func) {
		return new AnyFilter(BasicExpression.fromOExpression(func.getSource()), func.getVariable(),
				BasicFilter.fromExpression(func.getPredicate()));
	}
}
