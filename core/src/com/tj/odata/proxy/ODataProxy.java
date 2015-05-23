package com.tj.odata.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.odata4j.producer.ODataProducer;


public class ODataProxy implements InvocationHandler{

	private Object service;
	public ODataProxy(ODataProducer service2) {
		this.service=service2;
	}
	public ODataProxy(Object proxy) {
		this.service=proxy;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(service, args);
		} catch(InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}
