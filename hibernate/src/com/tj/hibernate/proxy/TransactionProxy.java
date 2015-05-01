package com.tj.hibernate.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.odata4j.producer.ODataProducer;

import com.tj.odata.proxy.ODataProxy;


public class TransactionProxy extends ODataProxy{
	private SessionFactory[] sessionFactories;
	private Object proxyObject;

	public TransactionProxy(ODataProducer service, SessionFactory... session) {
		super(service);
		this.proxyObject=service;
		sessionFactories=session;
	}
	public TransactionProxy(Object service, SessionFactory... session) {
		super(service);
		this.proxyObject=service;
		sessionFactories=session;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Session[] sessions=new Session[sessionFactories.length];
		int i=0;
		for(SessionFactory fact : sessionFactories) {
			sessions[i++]=fact.getCurrentSession();
		}
		try {
			for(Session session : sessions) {
				if(!session.getTransaction().isActive()) {
					session.beginTransaction();
				}
			}
			Object ret=method.invoke(proxyObject, args);
			for(Session session : sessions) {
				session.getTransaction().commit();
			}
			return ret;
		} catch(InvocationTargetException e) {
			for(Session session : sessions) {
				session.getTransaction().rollback();
			}
			throw e.getTargetException();
		} finally {
			for(Session session : sessions) {
				if(session.isOpen()) {
					session.close();
				}
			}
		}
	}

}
