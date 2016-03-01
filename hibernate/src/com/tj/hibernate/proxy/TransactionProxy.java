package com.tj.hibernate.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.odata4j.producer.ODataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tj.odata.proxy.ODataProxy;

/***
 * Responsible for creating a transaction for a call to a service or
 * producer. It checks to see if transaction has been created at a higher level first.
 * 
 * @author Admin
 *
 */
public class TransactionProxy extends ODataProxy {
	private SessionFactory[] sessionFactories;
	private Object proxyObject;

	private static Logger log = LoggerFactory.getLogger(TransactionProxy.class);

	public TransactionProxy(ODataProducer service, SessionFactory... session) {
		super(service);
		this.proxyObject = service;
		sessionFactories = session;
	}

	public TransactionProxy(Object service, SessionFactory... session) {
		super(service);
		this.proxyObject = service;
		sessionFactories = session;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Session[] sessions = new Session[sessionFactories.length];
		int i = 0;
		for (SessionFactory fact : sessionFactories) {
			sessions[i++] = fact.getCurrentSession();
		}
		boolean commit = false;
		try {
			for (Session session : sessions) {
				if (!session.getTransaction().isActive()) {
					session.beginTransaction();
					commit = true;
					log.debug("Starting transaction... (Method: {}, Class:{})", method.getName(),
							proxyObject.getClass());
				}
			}
			log.info("Invoking method in class {}: {}", proxy.getClass(), method.getName());
			Object ret = method.invoke(proxyObject, args);
			for (Session session : sessions) {
				if (commit) {
					log.debug("Committing transaction... (Method: {}, Class:{})", method.getName(),
							proxyObject.getClass());
					session.getTransaction().commit();
				}
			}
			return ret;
		} catch (InvocationTargetException e) {
			for (Session session : sessions) {
				if (commit) {
					log.debug("Rolling Back Transaction... (Method: {}, Class:{})", method.getName(),
							proxyObject.getClass());
					session.getTransaction().rollback();
				}
			}
			log.error("Exception In method invocation.", e);
			throw e.getTargetException();
		} finally {
			/*
			 * for(Session session : sessions) {
			 * if(session.isOpen()) {
			 * session.close();
			 * }
			 * }
			 */
		}
	}

}
