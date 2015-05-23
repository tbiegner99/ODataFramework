package com.tj.dao.hibernate;

import org.hibernate.SessionFactory;

import com.tj.dao.DAOBase;
import com.tj.odata.service.GenericDAOService;

<<<<<<< HEAD
public abstract class AbstractHibernateDAOService<T> extends GenericDAOService<T> {
	private SessionFactory factory;

	public AbstractHibernateDAOService(DAOBase<T> dao, SessionFactory factory) {
		super(dao);
		this.factory = factory;
=======

public abstract class AbstractHibernateDAOService<T> extends GenericDAOService<T>{
	private SessionFactory factory;
	public AbstractHibernateDAOService(DAOBase<T> dao, SessionFactory factory) {
		super(dao);
		this.factory=factory;
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
	}

	@Override
	public com.tj.odata.service.Service<?> getProxy() {
		return super.getProxy();
<<<<<<< HEAD
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

=======
		/*List<Class<?>> interfaces=new ArrayList<>(Arrays.asList(this.getClass().getInterfaces()));
		interfaces.add(0, Service.class);
		Class<?>[] interfaceArray=interfaces.toArray(new Class<?>[interfaces.size()]);
		return (com.tj.odata.service.Service<?>) Proxy.newProxyInstance(
						com.tj.odata.service.Service.class.getClassLoader(),
						interfaceArray,
						new TransactionProxyFactory(factory).getServiceProxy(this)
						);*/

	}
	public SessionFactory getSessionFactory() {return this.factory;}
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
}
