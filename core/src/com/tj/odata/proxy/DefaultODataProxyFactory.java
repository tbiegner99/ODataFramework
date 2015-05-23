package com.tj.odata.proxy;



public class DefaultODataProxyFactory implements ODataProxyFactory{

	@Override
	public ODataProxy getServiceProxy(Object service) {
		return new ODataProxy(service);
	}

}
