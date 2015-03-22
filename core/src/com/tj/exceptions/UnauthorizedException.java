package com.tj.exceptions;

import javax.ws.rs.core.Response.StatusType;

import org.odata4j.exceptions.ODataProducerException;

import com.sun.jersey.api.client.ClientResponse.Status;

public class UnauthorizedException extends ODataProducerException {
	private static final long serialVersionUID = -8549512734803704326L;

	public UnauthorizedException(String message, Throwable e) {
		super(message, e);
	}

	public UnauthorizedException(String message) {
		super(message, null);
	}

	public UnauthorizedException() {
		this("");
	}

	@Override
	public StatusType getHttpStatus() {
		return Status.UNAUTHORIZED;
	}
}
