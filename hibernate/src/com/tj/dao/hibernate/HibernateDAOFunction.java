package com.tj.dao.hibernate;

import org.hibernate.Session;

import com.tj.odata.functions.Function;

public interface HibernateDAOFunction extends Function {
	public void setSession(Session session);
}
