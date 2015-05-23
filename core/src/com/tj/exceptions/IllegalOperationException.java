package com.tj.exceptions;

import javax.ws.rs.core.Response.StatusType;

import org.odata4j.exceptions.ODataProducerException;

import com.sun.jersey.api.client.ClientResponse.Status;

public class IllegalOperationException extends ODataProducerException {

	private static final long serialVersionUID = 8749162116272060911L;

	public IllegalOperationException(String message) {
		super(message, null);
	}

	public IllegalOperationException(String message, Throwable e) {
		super(message, e);
	}

	public IllegalOperationException() {
		this("");
	}

	@Override
	public StatusType getHttpStatus() {
		return Status.SERVICE_UNAVAILABLE;
	}
}
