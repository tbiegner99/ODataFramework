package com.tj.odata.proxy;

import java.util.Collection;

//TODO: this or use with service proxy
public interface ResultsInterceptor<T> {
	public T afterGet();
	public Collection<T> afterList(Collection<T> results);
	public boolean beforeDelete();
	public void afterDelete(T deletedEntity);
	public void beforeUpdate(T updateEntity);
	public void afterUpdate();

}
