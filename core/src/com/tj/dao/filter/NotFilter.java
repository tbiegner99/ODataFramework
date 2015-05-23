package com.tj.dao.filter;

import org.odata4j.expression.NotExpression;

public class NotFilter implements Filter {

	private Filter filter;

	public NotFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public static Filter fromExpression(NotExpression e) {
		return new NotFilter(BasicFilter.fromExpression(e.getExpression()));
	}

}
