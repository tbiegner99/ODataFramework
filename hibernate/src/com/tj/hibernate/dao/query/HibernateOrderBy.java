package com.tj.hibernate.dao.query;

import java.util.List;

import org.odata4j.expression.OrderByExpression;

import com.tj.dao.filter.Expression;
import com.tj.dao.filter.OrderBy;
import com.tj.dao.filter.OrderClause;
import com.tj.dao.filter.PropertyExpression;

public class HibernateOrderBy extends OrderBy {
	public HibernateOrderBy() {
	}

	public HibernateOrderBy(List<OrderByExpression> orderByExpressions) {
		super(orderByExpressions);
	}

	@Override
	public String asString() {
		if (getClauses().isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder("ORDER BY ");
		List<OrderClause> clauses = getClauses();
		for (int i = 0; i < clauses.size(); i++) {
			OrderClause clause = clauses.get(i);
			sb.append(expressionToString(clause.getExp()));
			switch (clause.getOrder()) {
				case ASCENDING:
					sb.append(" asc");
					break;
				case DESCENDING:
					sb.append(" desc");
					break;
			}
			if (i < clauses.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public String expressionToString(Expression exp) {
		if (exp.getClass() == PropertyExpression.class) {
			return "c." + ((PropertyExpression) exp).getProperty().replace('/', '.');
		} else {
			throw new RuntimeException("Only Property expression is supported");
		}
	}

}
