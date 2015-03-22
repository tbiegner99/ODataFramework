package com.tj.dao.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tj.odata.functions.DefaultFunctionFactory;

public class HibernateFunctionFactory extends DefaultFunctionFactory<HibernateDAOFunction> {
	private SessionFactory factory;
	private Session session;

	public HibernateFunctionFactory(SessionFactory factory, Collection<HibernateDAOFunction> functions) {
		super(functions);
		for (HibernateDAOFunction func : functions) {
			func.setSession(this.getSession());
		}
	}

	protected HibernateFunctionFactory(SessionFactory factory) {
		this.factory = factory;
	}

	protected void addItem(HibernateDAOFunction item) {
		super.addItem(item);
		item.setSession(getSession());
	}

	protected Session getSession() {
		if (session == null || !session.isOpen()) {
			session = factory.openSession();
		}
		return session;
	}
}
