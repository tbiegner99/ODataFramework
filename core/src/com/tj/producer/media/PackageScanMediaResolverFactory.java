package com.tj.producer.media;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.reflections.Reflections;

import com.tj.exceptions.IllegalRequestException;
import com.tj.producer.annotations.MediaResolverFor;

@SuppressWarnings("rawtypes")
public class PackageScanMediaResolverFactory extends MediaResolverFactory {
	private Map<Class<?>, Class<? extends GenericMediaResolver>> mediaResolver;
	private PackageScanMediaResolverFactory(String... packages) {
		mediaResolver=new HashMap<Class<?>, Class<? extends GenericMediaResolver>>();
		init(packages);
	}
	private void init(String... packages) {
		for(String pack : packages) {
			Reflections ref=new Reflections(pack);
			for(Class<? extends GenericMediaResolver> type  : ref.getSubTypesOf(GenericMediaResolver.class)) {
				if(type.isAnnotationPresent(MediaResolverFor.class)) {
					Class<?> resolverFor=type.getAnnotation(MediaResolverFor.class).type();
					mediaResolver.put(resolverFor, type);
				}
			}

		}

	}
	public static PackageScanMediaResolverFactory createForPackages(String... packages) {
		return new PackageScanMediaResolverFactory(packages);
	}
	@Override
	public MediaResolver createMediaResolver(Object entity) {
		if(entity==null) {return null;}
		Class<?> entityClass=entity.getClass();
		while(entityClass!=null) {
			if(mediaResolver.containsKey(entityClass)) {
				return createMediaResolverFor(entity, entityClass, mediaResolver.get(entityClass));
			}
			entityClass=entityClass.getSuperclass();
		}
		throw new IllegalRequestException(entity.getClass().getSimpleName()+" is not a supported media type");
	}
	@Override
	public Collection<Class<?>> getSupportedClasses() {
		return mediaResolver.keySet();
	}


	public GenericMediaResolver<?> createMediaResolverFor(Object entity,Class<?> entityClass, Class<? extends GenericMediaResolver> resolverClass) {
		try {
			return resolverClass.getConstructor(entityClass).newInstance(entity);
		} catch (Exception e) {
			//Log
			e.printStackTrace();
		}
		return null;
	}

}
