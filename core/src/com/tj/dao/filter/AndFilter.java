package com.tj.dao.filter;

import org.odata4j.expression.AndExpression;

/***
 * Filter representing logical and of 2 filter expressions
 * 
 * @author Admin
 *
 */
public class AndFilter extends CompositeFilter {

	public AndFilter(Filter f1, Filter f2) {
		super(f1, f2);
	}

	public static Filter fromExpression(AndExpression e) {
		Filter lhs = BasicFilter.fromExpression(e.getLHS());
		Filter rhs = BasicFilter.fromExpression(e.getRHS());
		return new AndFilter(lhs, rhs);
	}

}
