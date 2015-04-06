package com.tj.producer;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.ODataProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tj.exceptions.NoLoginException;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.User;
import com.tj.security.UserResolver;

public class GenericProducerFactory implements ODataProducerFactory {
	private ProducerConfiguration config;
	private UserResolver<?> userResolver;
	private CompositeSecurityManager securityManager;
	private EdmDataServices metadata;
	private GenericEdmGenerator edmGenerator;

	public GenericProducerFactory() {
	}

	public GenericProducerFactory(ProducerConfiguration cfg, CompositeSecurityManager manager, UserResolver<?> resolver) {
		config = cfg;
		userResolver = resolver;
		securityManager = manager;
	}

	public GenericProducerFactory(ProducerConfiguration cfg, CompositeSecurityManager manager) {
		this(cfg, manager, null);
	}

	public GenericProducerFactory(ProducerConfiguration cfg) {
		this(cfg, null);
	}

	@Override
	public ODataProducer create(Properties properties) {
		GenericProducer p = new GenericProducer(config,metadata);
		return p;
	}

	public ODataProducer createForRequest(HttpServletRequest request, ResponseContext response,
			SecurityContext security, Properties properties) {
		User user = null;
		if (userResolver != null) {
			try {
				user = userResolver.getUser(request, security);
			} catch (NoLoginException e) {
				// continue maybe log here. it is the responsibility of the producer to handle non logins
			}
		}
		GenericProducer p = new GenericProducer(request, response, securityManager, user, config,metadata);
		return p;
	}

	@Autowired(required=false)
	public void setConfig(ProducerConfiguration config) {
		this.config = config;
		edmGenerator=new GenericEdmGenerator(config);
		metadata=edmGenerator.generateEdm(null).build();
	}

	@Autowired(required=false)
	public void setUserResolver(UserResolver<?> userResolver) {
		this.userResolver = userResolver;
	}

	@Autowired(required=false)
	public void setSecurityManager(CompositeSecurityManager securityManager) {
		this.securityManager = securityManager;
	}

}
