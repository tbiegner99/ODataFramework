package com.tj.odata.service;

import java.lang.reflect.Proxy;

import com.tj.odata.proxy.DefaultODataProxyFactory;
import com.tj.odata.proxy.ProxyService;

/***
 * Provides a default proxy for any service that extends this class. Every class provided by this class extends this.
 * 
 * @author tbiegner
 *
 * @param <T>
 */

public abstract class AbstractService<T> implements ProxyService<T> {
	@Override
	public ProxyService<?> getProxy() {
		return (ProxyService<?>) Proxy.newProxyInstance(Service.class.getClassLoader(),
				new Class[] { ProxyService.class }, new DefaultODataProxyFactory().getServiceProxy(this));
	}

	public ProxyService<?> getImplementor() {
		return this;
	}
}
