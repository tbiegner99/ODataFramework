package com.tj.producer.media;


public abstract class GenericMediaResolver<T> implements MediaResolver{

	private T entity;
	public GenericMediaResolver(T entity) {
		this.entity=entity;
	}
	public T getEntity() {return entity;}
	public abstract Class<T> getResolverType();
}
