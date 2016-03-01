package com.tj.hibernate.proxy;

import org.hibernate.SessionFactory;

import com.tj.odata.proxy.ODataProxy;
import com.tj.odata.proxy.ODataProxyFactory;

public class TransactionProxyFactory implements ODataProxyFactory {

	private SessionFactory[] factories;

	public TransactionProxyFactory(SessionFactory... factories) {
		this.factories = factories;
	}

	@Override
	public ODataProxy getServiceProxy(Object producer) {
		return new TransactionProxy(producer, factories);
	}

}
