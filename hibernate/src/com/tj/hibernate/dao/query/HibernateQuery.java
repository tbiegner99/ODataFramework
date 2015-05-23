package com.tj.hibernate.dao.query;

import org.hibernate.Session;

import com.tj.dao.filter.Query;
import com.tj.security.SecurityManager;
import com.tj.security.user.User;

public class HibernateQuery<T> extends Query<T> {

	public HibernateQuery(QueryType type, Class<T> entity) {
		super(type, entity);
		setWhere(new HibernateWhereClause(this));
	}

	public HibernateQuery(QueryType type, Class<T> entity, SecurityManager<T, User> manager, User user) {
<<<<<<< HEAD
		super(type, entity, -1, -1, manager, user);
=======
		super(type, entity,-1,-1,manager,user);
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
		setWhere(new HibernateWhereClause(this));
	}

	public HibernateQuery(QueryType type, Class<T> entity, int top, int skip) {
		super(type, entity, top, skip);
		setWhere(new HibernateWhereClause(this));
	}

	public HibernateQuery(QueryType type, Class<T> entity, int top, int skip, SecurityManager<T, User> manager,
			User user) {
		super(type, entity, top, skip, manager, user);
		setWhere(new HibernateWhereClause(this));
	}

	@Override
	public String asString() {
		String ret = "";
		switch (getQueryType()) {
			case CREATE:
				ret = " INSERT INTO ";
				break;
			case DELETE:
				ret = "DELETE ";
				break;
			case COUNT:
				ret = "SELECT COUNT(c) ";
				break;
			case RETRIEVE:
				ret = "SELECT " + (isCount() ? "COUNT(c) " : "c ");
				break;
			case UPDATE:
				ret = "UPDATE c ";
				break;
		}
<<<<<<< HEAD
		ret += "from " + getEntityType().getName() + " c ";
=======
		ret += "from " + getEntityType().getSimpleName() + " c ";
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
		if (getWhere() != null) {
			ret += getWhere().asString() + " ";
		}
		if (getOrderByClause() != null) {
			ret += getOrderByClause().asString();
		}
		return ret;
	}

	public org.hibernate.Query asHibernateQuery(Session sess) {
		org.hibernate.Query ret = sess.createQuery(asString());
		if (getTop() > 0) {
			ret = ret.setMaxResults(getTop());
		}
		if (getSkip() >= 0) {
			ret = ret.setFirstResult(getSkip());
		}
		for (Parameter p : getParameters()) {
<<<<<<< HEAD
			ret.setParameter(p.getName(), p.getValue());
=======
				ret.setParameter(p.getName(), p.getValue());
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
		}
		return ret;
	}
}
