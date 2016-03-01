package com.tj.odata.service;

import java.lang.reflect.Proxy;

import com.tj.odata.proxy.DefaultODataProxyFactory;
import com.tj.odata.proxy.ProxyService;
import com.tj.producer.configuration.ServiceProducerConfiguration;

/***
 * Provides a default proxy for any service that extends this class. Every class provided by this class extends this.
 * 
 * @author tbiegner
 *
 * @param <T>
 */

public abstract class AbstractService<T> implements ProxyService<T> {

	private ServiceProducerConfiguration configuration;

	private boolean useProxy = false;

	public void setConfiguration(ServiceProducerConfiguration configuration) {
		this.configuration = configuration;
	}

	public ServiceProducerConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public ProxyService<?> getProxy() {
		return (ProxyService<?>) Proxy.newProxyInstance(Service.class.getClassLoader(),
				new Class[] { ProxyService.class }, new DefaultODataProxyFactory().getServiceProxy(this));
	}

	public ProxyService<?> getImplementor() {
		return this;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}

}
