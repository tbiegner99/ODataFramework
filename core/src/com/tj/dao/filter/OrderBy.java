package com.tj.dao.filter;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.expression.OrderByExpression;

public abstract class OrderBy {
	public static enum Order {
		ASCENDING, DESCENDING
	}

	private List<OrderClause> clauses;

	public OrderBy() {
		clauses = new ArrayList<OrderClause>();
	}

	public OrderBy(List<OrderByExpression> orderByExpressions) {
		this();
		createClauses(orderByExpressions);
	}

	private void createClauses(List<OrderByExpression> orderByExpressions) {
		for (OrderByExpression o : orderByExpressions) {
			addClause(getOrderBy(o));
		}
	}

	private OrderClause getOrderBy(OrderByExpression o) {
		return OrderClause.fromOrderByExpression(o);
	}

	public void addClause(OrderByExpression clause) {
		clauses.add(getOrderBy(clause));
	}

	public void addClause(OrderClause clause) {
		clauses.add(clause);
	}

	public List<OrderClause> getClauses() {
		return clauses;
	}

	public abstract String asString();
}
