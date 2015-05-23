package com.tj.dao.filter;

import org.odata4j.expression.OrExpression;

public class OrFilter extends CompositeFilter {

	public OrFilter(Filter lhs, Filter rhs) {
		super(lhs, rhs);
	}

	public static Filter fromExpression(OrExpression e) {
		Filter lhs = BasicFilter.fromExpression(e.getLHS());
		Filter rhs = BasicFilter.fromExpression(e.getRHS());
		return new OrFilter(lhs, rhs);
	}

}
