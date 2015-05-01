package com.tj.hibernate.proxy;

import org.hibernate.SessionFactory;

import com.tj.producer.GenericProducerFactory;


public class HibernateTransactionProducerFactory extends  GenericProducerFactory{

	public HibernateTransactionProducerFactory(SessionFactory... factories) {
		setProxy(new TransactionProxyFactory(factories));
		setExtensionResolver(new HibernateProducerExtensionResolver(factories));
	}

}
