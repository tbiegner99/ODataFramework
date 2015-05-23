package com.tj.exceptions;

import javax.ws.rs.core.Response.StatusType;

import org.odata4j.exceptions.ODataProducerException;

import com.sun.jersey.api.client.ClientResponse.Status;

public class NotFoundException extends ODataProducerException {

	private static final long serialVersionUID = -3631742766745468914L;

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);

	}

	public NotFoundException(String message) {
		super(message, null);

	}

	public NotFoundException() {
		this("");
	}

	@Override
	public StatusType getHttpStatus() {
		return Status.NOT_FOUND;
	}

}
