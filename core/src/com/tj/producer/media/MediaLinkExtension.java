package com.tj.producer.media;

import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.HttpHeaders;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.producer.EntityQueryInfo;
import org.odata4j.producer.ODataContext;
import org.odata4j.producer.OMediaLinkExtension;
import org.odata4j.producer.QueryInfo;

import com.tj.exceptions.IllegalOperationException;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.producer.configuration.ProducerConfiguration.Action;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.user.User;

public class MediaLinkExtension implements OMediaLinkExtension {
	private ProducerConfiguration config;
	private CompositeSecurityManager securityManager;
	private User requestUser;

	public MediaLinkExtension(ProducerConfiguration cfg, CompositeSecurityManager manager, User user) {
		config = cfg;
		this.securityManager = manager;
		this.requestUser = user;
	}

	public MediaResolver getMediaResolver(ODataContext context, OEntity mle) {
		return getMediaResolver(mle, context, config.getEntitySetClass(mle.getEntitySetName()));
	}

	public MediaResolver getMediaResolver(OEntity oentity, ODataContext context, Class<?> clazz) {
		Object entity = getRawEntity(context, oentity);
		MediaResolverFactory factory = config.getMediaResolverFactory(clazz);
		if (factory == null) {
			throw new RuntimeException("Could not create media resolver for entity: " + clazz.getName());
		}
		return factory.createMediaResolver(entity);
	}

	public Object getRawEntity(ODataContext ocontext, OEntity mle) {
		String entitySetName = mle.getEntitySetName();
		Class<?> type = config.getEntitySetClass(entitySetName);
		RequestContext request = RequestContext.createRequestContext(ocontext, mle.getEntityKey(), type, null,
				securityManager, requestUser);
		return config.invoke(entitySetName, Action.GET, request, new ResponseContext.DefaultResponseContext());
	}

	@Override
	public InputStream getInputStreamForMediaLinkEntry(ODataContext odataContext, OEntity mle, String etag,
			EntityQueryInfo query) {

		return getMediaResolver(odataContext, mle).getMedia(odataContext);
	}

	@Override
	public OutputStream getOutputStreamForMediaLinkEntryCreate(ODataContext odataContext, OEntity mle, String etag,
			QueryInfo query) {
		throw new IllegalOperationException("Not yet implemented");
	}

	@Override
	public OutputStream getOutputStreamForMediaLinkEntryUpdate(ODataContext odataContext, OEntity mle, String etag,
			QueryInfo query) {
		throw new IllegalOperationException("Not yet implemented");
	}

	@Override
	public void deleteStream(ODataContext odataContext, OEntity mle, QueryInfo query) {
		getMediaResolver(odataContext, mle).deleteMedia(odataContext);
	}

	@Override
	public String getMediaLinkContentType(ODataContext taContext, OEntity mle) {
		return getMediaResolver(taContext, mle).getMediaContentType(taContext);
	}

	@Override
	public String getMediaLinkContentDisposition(ODataContext odataContext, OEntity mle) {
		return getMediaResolver(odataContext, mle).getMediaContentDisposition(odataContext);
	}

	@Override
	public OEntity createMediaLinkEntry(ODataContext odataContext, EdmEntitySet entitySet, HttpHeaders httpHeaders) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OEntity getMediaLinkEntryForUpdateOrDelete(ODataContext odataContext, EdmEntitySet entitySet,
			OEntityKey key, HttpHeaders httpHeaders) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OEntity updateMediaLinkEntry(ODataContext odataContext, OEntity mle, OutputStream outStream) {
		// TODO Auto-generated method stub
		return null;// getMediaResolver().updateMedia(odataContext, outStream);
	}

}
