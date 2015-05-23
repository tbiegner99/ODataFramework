package com.tj.producer.media;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.odata4j.producer.ODataContext;

public class ReflectionMediaResolver implements MediaResolver {
	private ReflectionResolver dataResolver, contentType, contentDisposition;
	private Object entity;

	public ReflectionMediaResolver(Object entity, ReflectionResolver data, ReflectionResolver type,
			ReflectionResolver disposition) {
		this.entity = entity;
		dataResolver = data;
		contentType = type;
		contentDisposition = disposition;
	}

	@Override
	public void deleteMedia(ODataContext context) {
		dataResolver.setValue(entity, null);
		contentType.setValue(entity, null);
		contentDisposition.setValue(entity, null);
	}

	@Override
	public String getMediaContentType(ODataContext context) {
		Object contentType = this.contentType.getValue(entity, context);
		if (contentType != null) {
			return contentType.toString();
		}
		return null;
	}

	@Override
	public String getMediaContentDisposition(ODataContext context) {
		Object disposition = contentDisposition.getValue(entity);
		if (disposition != null) {
			return disposition.toString();
		}
		return null;
	}

	@Override
	public InputStream getMedia(ODataContext context) {
		Object out = dataResolver.getValue(entity, context);
		if (out == null) {
			return new ByteArrayInputStream(new byte[0]);
		}
		if (out instanceof InputStream) {
			return (InputStream) out;
		}
		if (out instanceof byte[]) {
			return new ByteArrayInputStream((byte[]) out);
		}
		if (out instanceof Byte[]) {
			Byte[] result = (Byte[]) out;
			byte[] inputStream = new byte[result.length];
			for (int i = 0; i < result.length; i++) {
				inputStream[i] = (result[i] == null ? 0 : result[i].byteValue());
			}
			return new ByteArrayInputStream(inputStream);
		}
		return new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void updateMedia(ODataContext context, InputStream media) {

	}
}
