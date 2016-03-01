package com.tj.exceptions;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.core.OError;
import org.odata4j.exceptions.ODataProducerException;

public class ValidationException extends ODataProducerException {

	private static final long serialVersionUID = 8537059502874671892L;

	public ValidationException() {
		this(null, null);
	}

	public ValidationException(String message) {
		this(message, null);
	}

	public ValidationException(Throwable cause) {
		this(null, cause);
	}

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public StatusType getHttpStatus() {
		return Status.NOT_ACCEPTABLE;
	}

	private ValidationException(OError error) {
		super(error);
	}
}
