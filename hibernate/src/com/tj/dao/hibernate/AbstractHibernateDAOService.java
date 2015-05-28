package com.tj.dao.hibernate;

import org.hibernate.SessionFactory;

import com.tj.dao.DAOBase;
import com.tj.odata.proxy.ProxyService;
import com.tj.odata.service.GenericDAOService;

public abstract class AbstractHibernateDAOService<T> extends GenericDAOService<T> {
	private SessionFactory factory;

	public AbstractHibernateDAOService(DAOBase<T> dao, SessionFactory factory) {
		super(dao);
		this.factory = factory;
	}

	@Override
	public ProxyService<?> getProxy() {
		return super.getProxy();
		/*
		 * List<Class<?>> interfaces=new ArrayList<>(Arrays.asList(this.getClass().getInterfaces())); interfaces.add(0,
		 * Service.class); Class<?>[] interfaceArray=interfaces.toArray(new Class<?>[interfaces.size()]); return
		 * (com.tj.odata.service.Service<?>) Proxy.newProxyInstance(
		 * com.tj.odata.service.Service.class.getClassLoader(), interfaceArray, new
		 * TransactionProxyFactory(factory).getServiceProxy(this) );
		 */

	}

	public SessionFactory getSessionFactory() {
		return this.factory;
	}

	public void setFactory(SessionFactory factory) {
		this.factory = factory;

	}

}
