package com.tj.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.core.OError;
import org.odata4j.exceptions.ODataProducerException;


public class IllegalTypeException extends ODataProducerException {

	private static final long serialVersionUID = 1916538740831878279L;

	public IllegalTypeException(OError error) {
		super(error);
	}

	public IllegalTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalTypeException(String message) {
		super(message,null);
	}

	@Override
	public StatusType getHttpStatus() {
		return Response.Status.BAD_REQUEST;
	}
}
