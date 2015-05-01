package com.tj.hibernate.proxy;

import org.hibernate.SessionFactory;
import org.odata4j.core.OExtension;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.OMediaLinkExtensions;

import com.tj.producer.ProducerExtensionResolver;
import com.tj.producer.application.ApplicationMediaLinkExtensions;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.user.User;


public class HibernateProducerExtensionResolver implements ProducerExtensionResolver{

	private SessionFactory[] factories;
	public HibernateProducerExtensionResolver(SessionFactory... factories) {
		this.factories=factories;
	}
	@Override
	public <TExtension extends OExtension<ODataProducer>> TExtension findExtension(Class<TExtension> clazz, ProducerConfiguration config, CompositeSecurityManager securityContext, User userContext) {
		if (clazz == OMediaLinkExtensions.class) {
			return (TExtension) new ApplicationMediaLinkExtensions(config, securityContext, userContext,new TransactionProxyFactory(factories));
		}
		return null;
	}
}
