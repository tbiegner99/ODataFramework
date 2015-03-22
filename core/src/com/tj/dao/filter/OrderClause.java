package com.tj.dao.filter;

import org.odata4j.expression.OrderByExpression;

import com.tj.dao.filter.OrderBy.Order;

public class OrderClause {
	private Order order = Order.ASCENDING;
	private Expression exp;

	public OrderClause(Order order, BasicExpression expression) {
		this(expression);
		this.order = order;
	}

	public OrderClause(Expression expression) {
		this.exp = expression;
	}

	public Order getOrder() {
		return order;
	}

	public Expression getExp() {
		return exp;
	}

	public static OrderClause fromOrderByExpression(OrderByExpression ob) {
		OrderClause ret = new OrderClause(BasicExpression.fromOExpression(ob.getExpression()));
		switch (ob.getDirection()) {
			case DESCENDING:
				ret.order = Order.DESCENDING;
				break;
			default:
				break;

		}
		return ret;
	}
}
