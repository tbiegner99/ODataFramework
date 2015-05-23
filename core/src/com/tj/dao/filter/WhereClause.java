package com.tj.dao.filter;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.expression.CommonExpression;

public abstract class WhereClause {
	private List<Filter> clauses;
	private List<Query.Parameter> parameters;
	private Query<?> owner;

	public WhereClause(Query<?> owner) {
		if(owner==null) {
			throw new IllegalArgumentException("Where clause must have a parent query.");
		}
		clauses = new ArrayList<Filter>();
		parameters = new ArrayList<Query.Parameter>();
		this.owner=owner;
	}

	public Filter[] getClauses() {
		Filter[] ret = new Filter[clauses.size()];
		return clauses.toArray(ret);
	}

	public List<Filter> getClausesCollection() {
		return clauses;
	}

	public List<Filter> getClausesWithSecurity() {
		ArrayList<Filter> filter=new ArrayList<Filter>(getClausesCollection());
		filter.addAll(owner.getSecurityFilters());
		return filter;
	}

	public List<Query.Parameter> getParameters() {
		return parameters;
	}

	public void add(CommonExpression exp) {
		if (exp == null) {
			return;
		}
		clauses.add(BasicFilter.fromExpression(exp));
	}

	protected Query<?> getOwner() {
		return owner;
	}

	public abstract String asString();

}
