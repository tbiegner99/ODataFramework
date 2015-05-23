package com.tj.producer.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.odata4j.producer.ODataProducer;

import com.sun.jersey.spi.inject.Inject;
import com.tj.producer.GenericProducerFactory;
import com.tj.producer.HttpServletResponseContext;

@Provider
public class ProducerResolver implements ContextResolver<ODataProducer> {

	@Inject
	private GenericProducerFactory factory;

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	@Context
	private SecurityContext securityContext;

	@Override
	public ODataProducer getContext(Class<?> type) {
		return factory.createForRequest(request, new HttpServletResponseContext(response), securityContext, null);
	}
}
