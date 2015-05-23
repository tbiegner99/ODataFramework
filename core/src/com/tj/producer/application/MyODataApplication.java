package com.tj.producer.application;

import java.util.Set;

import org.odata4j.jersey.producer.resources.ODataApplication;
import org.odata4j.jersey.producer.resources.ODataProducerProvider;
import org.odata4j.producer.resources.ExceptionMappingProvider;

public class MyODataApplication extends ODataApplication {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = super.getClasses();
		classes.remove(ODataProducerProvider.class);
		classes.remove(ExceptionMappingProvider.class);
		classes.add(ExceptionTranslator.class);
		classes.add(ProducerResolver.class);
		return classes;
	}
}
