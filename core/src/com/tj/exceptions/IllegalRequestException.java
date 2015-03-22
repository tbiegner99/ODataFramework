package com.tj.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.core.OError;
import org.odata4j.exceptions.ODataProducerException;


public class IllegalRequestException extends ODataProducerException {

	private static final long serialVersionUID = -4561647549519223160L;

	public IllegalRequestException(OError error) {
		super(error);
	}

	public IllegalRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalRequestException(String message) {
		super(message,null);
	}

	@Override
	public StatusType getHttpStatus() {
		return Response.Status.BAD_REQUEST;
	}

}
