package com.tj.dao.filter;

import org.odata4j.expression.AggregateAllFunction;

/***
 * Filter for an odata all portion of a query. This is of the form:
 * 
 * source/all(variable: predicate)
 * 
 * This would evaluate to a boolean
 * 
 * @author Admin
 *
 */
public class AllFilter implements Filter {
	private Filter predicate;
	private Expression source;
	private String variable;

	public AllFilter(Expression source, String variable, Filter predicate) {
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

	public static AllFilter fromFilter(AggregateAllFunction func) {
		return new AllFilter(BasicExpression.fromOExpression(func.getSource()), func.getVariable(),
				BasicFilter.fromExpression(func.getPredicate()));
	}
}
