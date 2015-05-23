package com.tj.dao.hibernate;

import org.hibernate.SessionFactory;

import com.tj.odata.service.AbstractPackageScanService;


public abstract class AbstractHibernatePackageScanService extends AbstractPackageScanService{
	private SessionFactory factory;

	public AbstractHibernatePackageScanService(SessionFactory fact) {
		factory = fact;
	}
	//DONT overide getProxy for transaction management. That could result in nested transactions.
	@Override
	public com.tj.odata.service.Service<?> getProxy() {
		return super.getProxy();
		/*
		List<Class<?>> interfaces=new ArrayList<>(Arrays.asList(this.getClass().getInterfaces()));
		interfaces.add(0, Service.class);
		Class<?>[] interfaceArray=interfaces.toArray(new Class<?>[interfaces.size()]);
		return (com.tj.odata.service.Service<?>) Proxy.newProxyInstance(
						com.tj.odata.service.Service.class.getClassLoader(),
						interfaceArray,
						new TransactionProxyFactory(factory).getServiceProxy(this)
						);
	*/
	}
	protected SessionFactory getSessionFactory(){return factory;}
}
