package com.tj.producer.media;

import java.io.InputStream;

import org.odata4j.producer.ODataContext;

public class MediaEntityMediaResolver implements MediaResolver {

	private MediaEntity entity;

	public MediaEntityMediaResolver(MediaEntity entity) {
		this.entity = entity;
	}

	@Override
	public void deleteMedia(ODataContext context) {
		entity.deleteMedia(context);

	}

	@Override
	public String getMediaContentType(ODataContext context) {
		return entity.getMediaContentType(context);
	}

	@Override
	public String getMediaContentDisposition(ODataContext context) {
		return entity.getMediaContentDisposition(context);
	}

	@Override
	public InputStream getMedia(ODataContext context) {
		return entity.getMedia(context);
	}

	@Override
	public void updateMedia(ODataContext context, InputStream media) {
		entity.updateMedia(context, media);
	}

}
