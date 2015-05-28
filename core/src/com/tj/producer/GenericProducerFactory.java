package com.tj.producer;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.ODataProducerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tj.odata.proxy.ODataProxyFactory;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.user.UserResolver;

public class GenericProducerFactory implements ODataProducerFactory {
	private ProducerConfiguration config;
	private UserResolver<?> userResolver;
	private EdmDataServices metadata;
	private ODataProxyFactory proxy;
	private ProducerExtensionResolver extensionResolver;

	public GenericProducerFactory() {
	}

	public GenericProducerFactory(ProducerConfiguration cfg, UserResolver<?> resolver) {
		config = cfg;
		userResolver = resolver;
	}

	public GenericProducerFactory(ProducerConfiguration cfg) {
		this(cfg, null);
	}

	@Override
	public ODataProducer create(Properties properties) {
		GenericProducer p = new GenericProducer(config, metadata);
		if (proxy != null) {
			return createProducerProxy(p);
		}
		return p;
	}

	public ODataProducer createForRequest(HttpServletRequest request, ResponseContext response,
			SecurityContext security, Properties properties) {
		UserAwareODataProducer producer = create(request, response, config.getSecurityManager(), userResolver, config,
				metadata);
		producer.resolveUser(request);
		return producer;
	}

	public UserAwareODataProducer create(HttpServletRequest request, ResponseContext response,
			CompositeSecurityManager securityManager2, UserResolver<?> user, ProducerConfiguration config2,
			EdmDataServices metadata2) {
		UserAwareODataProducer producer = new GenericProducer(request, response, config.getSecurityManager(), user,
				config, metadata);
		((GenericProducer) producer).setExtensionResolver(extensionResolver);
		if (proxy != null) {
			producer = createProducerProxy((GenericProducer) producer);
		}
		producer.resolveUser(request);
		return producer;
	}

	private UserAwareODataProducer createProducerProxy(GenericProducer producer) {
		producer.setExtensionResolver(extensionResolver);
		List<Class<?>> interfaces = new ArrayList<>(Arrays.asList(producer.getClass().getInterfaces()));
		if (!interfaces.contains(UserAwareODataProducer.class)) {
			interfaces.add(0, UserAwareODataProducer.class);
		}
		Class<?>[] interfaceArray = interfaces.toArray(new Class<?>[interfaces.size()]);
		return (UserAwareODataProducer) Proxy.newProxyInstance(GenericProducer.class.getClassLoader(), interfaceArray,
				proxy.getServiceProxy(producer));
	}

	@Autowired(required = false)
	public void setConfig(ProducerConfiguration config) {
		this.config = config;
		metadata = config.getMetadata();
	}

	@Autowired(required = false)
	public void setUserResolver(UserResolver<?> userResolver) {
		this.userResolver = userResolver;
	}

	@Autowired(required = false)
	public void setProxy(ODataProxyFactory proxy) {
		this.proxy = proxy;
	}

	@Autowired(required = false)
	public void setExtensionResolver(ProducerExtensionResolver extensionResolver) {
		this.extensionResolver = extensionResolver;
	}

}
