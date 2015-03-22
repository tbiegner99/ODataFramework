package com.tj.dao.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.tj.security.SecurityManager;
import com.tj.security.User;

public abstract class Query<T> {
	public static enum QueryType {
		RETRIEVE, CREATE, UPDATE, DELETE, COUNT
	}

	private WhereClause where;
	private Class<T> entityType;
	private boolean count;
	private int top, skip;
	private QueryType queryType;
	private OrderBy orderBy;

	private List<Parameter> parameters;
	private SecurityManager<T, User> manager;
	private User userContext;

	public Query(QueryType type, Class<T> entity, int top, int skip) {
		this.count = type == QueryType.COUNT;
		queryType = this.count ? QueryType.RETRIEVE : type;
		entityType = entity;
		this.top = top;
		this.skip = skip;
		parameters = new ArrayList<Parameter>();
	}

	public Query(QueryType type, Class<T> entity) {
		this(type, entity, -1, -1);
	}

	public Query(QueryType type, Class<T> entity, int top, int skip, SecurityManager<T, User> manager, User userContext) {
		this(type, entity, top, skip);
		this.manager = manager;
		this.userContext = userContext;
	}

	public Query(QueryType type, Class<T> entity, SecurityManager<T, User> manager, User userContext) {
		this(type, entity);
		this.manager = manager;
		this.userContext = userContext;
	}

	public WhereClause getWhere() {
		return where;
	}

	public Class<?> getEntityType() {
		return entityType;
	}

	public int getTop() {
		return top;
	}

	public int getSkip() {
		return skip;
	}

	public boolean isCount() {
		return count;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	protected void setWhere(WhereClause where) {
		this.where = where;
	}

	protected void setEntityType(Class<T> entityType) {
		this.entityType = entityType;
	}

	protected void setCount(boolean count) {
		this.count = count;
	}

	protected void setTop(int top) {
		this.top = top;
	}

	protected void setSkip(int skip) {
		this.skip = skip;
	}

	protected void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}

	public SecurityManager<?, ?> getManager() {
		return manager;
	}

	public List<Parameter> getParameters() {
		parameters.clear();
		parameters.addAll(where.getParameters());
		return parameters;
	}

	public OrderBy getOrderByClause() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderByClause) {
		this.orderBy = orderByClause;
	}

	public Collection<? extends Filter> getSecurityFilters() {
		if (manager == null) {
			return new ArrayList<>();
		}
		Collection<? extends Filter> ret = manager.getUserLevelFilters(entityType, userContext);
		if (ret == null) {
			return new ArrayList<Filter>();
		}
		return ret;

	}

	public abstract String asString();

	public static class Parameter {
		private String name;
		private Class<?> type;
		private Object value;

		public Parameter(String name, Object value) {
			this.name = name;
			this.value = value;
			this.type=value.getClass();
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}

		public Class<?> getType() {
			return type;
		}

	}

}
