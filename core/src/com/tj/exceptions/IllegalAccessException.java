package com.tj.exceptions;

import javax.ws.rs.core.Response.StatusType;

import org.odata4j.exceptions.ODataProducerException;

import com.sun.jersey.api.client.ClientResponse.Status;

public class IllegalAccessException extends ODataProducerException {

	private static final long serialVersionUID = -3631742766745468914L;

	public IllegalAccessException(String message, Throwable cause) {
		super(message, cause);

	}

	public IllegalAccessException(String message) {
		super(message, null);

	}

	public IllegalAccessException() {
		this("");
	}

	@Override
	public StatusType getHttpStatus() {
		return Status.FORBIDDEN;
	}

}
