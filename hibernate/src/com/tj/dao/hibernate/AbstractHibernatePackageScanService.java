package com.tj.dao.hibernate;

import java.lang.reflect.Proxy;

import org.hibernate.SessionFactory;

import com.tj.hibernate.proxy.TransactionProxyFactory;
import com.tj.odata.proxy.ProxyService;
import com.tj.odata.service.AbstractPackageScanService;
import com.tj.odata.service.Service;

/**
 * Base class for hibernate services using package scan functionality. This supplies a getSessionFactory to its children
 * so that they can query. It also creates a proxy so that transactions can be managed.
 * 
 * @author Admin
 */
public abstract class AbstractHibernatePackageScanService extends AbstractPackageScanService {
	private SessionFactory factory;

	public AbstractHibernatePackageScanService(SessionFactory fact) {
		factory = fact;
	}

	@Override
	public ProxyService<?> getProxy() {
		return (ProxyService<?>) Proxy.newProxyInstance(Service.class.getClassLoader(),
				new Class[] { ProxyService.class }, new TransactionProxyFactory(factory).getServiceProxy(this));
	}

	protected SessionFactory getSessionFactory() {
		return factory;
	}
}
