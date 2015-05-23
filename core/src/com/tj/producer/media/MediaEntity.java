package com.tj.producer.media;

import java.io.InputStream;

import org.odata4j.producer.ODataContext;

public interface MediaEntity {
	public void deleteMedia(ODataContext context);

	public String getMediaContentType(ODataContext context);

	public String getMediaContentDisposition(ODataContext context);

	public InputStream getMedia(ODataContext context);

	public void updateMedia(ODataContext context, InputStream media);
}
