package com.tj.producer.media;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.tj.producer.annotations.entity.Binary;
import com.tj.producer.annotations.entity.ContentDisposition;
import com.tj.producer.annotations.entity.ContentType;

public abstract class MediaResolverFactory {

	public static MediaResolverFactory createFromClass(Class<?> clazz) {
		if (MediaEntity.class.isAssignableFrom(clazz)) {
			return new InterfaceMediaResolverFactory();
		} else {
			ReflectionResolver content = null, contentType = null, contentDisposition = null;
			for (Method m : clazz.getMethods()) {
				if (m.isAnnotationPresent(Binary.class)) {
					content = new MethodResolver(m);
					contentType = new StaticResolver(m.getAnnotation(Binary.class).contentType());
					contentDisposition = new StaticResolver(m.getAnnotation(Binary.class).contentType());
				}
				if (m.isAnnotationPresent(ContentType.class)) {
					contentType = new MethodResolver(m);
				}
				if (m.isAnnotationPresent(ContentDisposition.class)) {
					contentDisposition = new MethodResolver(m);
				}
			}
			for (Field f : clazz.getDeclaredFields()) {
				if (f.isAnnotationPresent(Binary.class)) {
					content = new FieldResolver(f);
					if(contentType==null) {
						contentType = new StaticResolver(f.getAnnotation(Binary.class).contentType());
					}
					if(contentDisposition==null) {
						contentDisposition = new StaticResolver(f.getAnnotation(Binary.class).contentType());
					}
				}
				if (f.isAnnotationPresent(ContentType.class)) {
					contentType = new FieldResolver(f);
				}
				if (f.isAnnotationPresent(ContentDisposition.class)) {
					ContentDisposition ann=f.getAnnotation(ContentDisposition.class);
					String replacement=ann.variable().isEmpty()?null:ann.variable();
					contentDisposition = new TemplateResolver(ann.defaultValue(),replacement, new FieldResolver(f));
				}
			}

			if (content != null && contentType != null) {
				return new ReflectionMediaResolverFactory(content, contentType, contentDisposition);
			}
		}
		return null;
	}

	public abstract MediaResolver createMediaResolver(Object entity);

	private static class InterfaceMediaResolverFactory extends MediaResolverFactory {

		@Override
		public MediaResolver createMediaResolver(Object entity) {
			return new MediaEntityMediaResolver((MediaEntity) entity);
		}

	}

	private static class ReflectionMediaResolverFactory extends MediaResolverFactory {

		private ReflectionResolver content, contentType, contentDisposition;

		public ReflectionMediaResolverFactory(ReflectionResolver content, ReflectionResolver contentType,
				ReflectionResolver contentDisposition) {
			this.content = content;
			this.contentDisposition = contentDisposition;
			this.contentType = contentType;
		}

		@Override
		public MediaResolver createMediaResolver(Object entity) {
			return new ReflectionMediaResolver(entity, content, contentType, contentDisposition);
		}

	}

}
