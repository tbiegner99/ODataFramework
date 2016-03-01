package com.tj.dao.hibernate;

import java.lang.reflect.Proxy;

import org.hibernate.SessionFactory;

import com.tj.dao.DAOBase;
import com.tj.hibernate.proxy.TransactionProxyFactory;
import com.tj.odata.proxy.ProxyService;
import com.tj.odata.service.GenericDAOService;
import com.tj.odata.service.Service;

/**
 * Base class for hibernate services. This supplies a getSessionFactory to its children
 * so that they can query. It also creates a proxy so that transactions can be managed.
 * 
 * @author Admin
 *
 * @param <T>
 *            the type of entity this services
 */
public abstract class AbstractHibernateDAOService<T> extends GenericDAOService<T> {
	private SessionFactory factory;

	public AbstractHibernateDAOService(DAOBase<T> dao, SessionFactory factory) {
		super(dao);
		this.factory = factory;
	}

	@Override
	public ProxyService<?> getProxy() {
		return (ProxyService<?>) Proxy.newProxyInstance(Service.class.getClassLoader(),
				new Class[] { ProxyService.class }, new TransactionProxyFactory(factory).getServiceProxy(this));

	}

	public SessionFactory getSessionFactory() {
		return this.factory;
	}

	public void setFactory(SessionFactory factory) {
		this.factory = factory;

	}

}
