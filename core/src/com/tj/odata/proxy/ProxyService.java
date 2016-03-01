package com.tj.odata.proxy;

import com.tj.odata.service.Service;

public interface ProxyService<T> extends Service<T> {
	ProxyService<?> getProxy();

	ProxyService<?> getImplementor();

	boolean isUseProxy();

	void setUseProxy(boolean useProxy);
}
