package com.tj.producer;

import org.odata4j.core.OExtension;
import org.odata4j.producer.ODataProducer;

import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.user.User;


public interface ProducerExtensionResolver {
	<TExtension extends OExtension<ODataProducer>> TExtension findExtension(Class<TExtension> clazz,ProducerConfiguration config,CompositeSecurityManager securityContext,User userContext);;
}
