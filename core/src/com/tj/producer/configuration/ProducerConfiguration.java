package com.tj.producer.configuration;

import java.util.Collection;
import java.util.Map;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmGenerator;

import com.tj.odata.functions.FunctionService;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.media.MediaResolverFactory;
import com.tj.security.CompositeSecurityManager;

public interface ProducerConfiguration extends FunctionService {
	public static enum Action {
		CREATE, GET, GET_MEDIA, UPDATE, DELETE, PATCH, LIST, COUNT
	}

	public <T> Object invoke(String entitySet, Action a, RequestContext request, ResponseContext response);

	public Class<?> getEntitySetClass(String entitySetName);

	public Collection<Class<?>> getEntityTypes();

	public Map<String, Class<?>> getKeysMap(String entitySetName);

	public boolean isMediaEntity(Class<?> clazz);

	public boolean doValidate();

	public MediaResolverFactory getMediaResolverFactory(Class<?> clazz);

	public void close();

	public int getMaxResults();

	public void setMaxResults(int maxResults);

	public CompositeSecurityManager getSecurityManager();

	public void setSecurityManager(CompositeSecurityManager securityManager);

	public EdmGenerator getEdmGenerator();

	public EdmDataServices getMetadata();

	public EdmDataServices refreshMetadata();

}