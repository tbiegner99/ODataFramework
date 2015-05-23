package com.tj.exceptions;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.exceptions.ODataProducerException;


public class DataConflictException extends ODataProducerException{

	private static final long serialVersionUID = 2306674022771981264L;

	public DataConflictException(Throwable cause) {
		super(cause.getLocalizedMessage(), cause);
	}

	public DataConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataConflictException(String message) {
		super(message, null);
	}

	@Override
	public StatusType getHttpStatus() {
		return Status.CONFLICT;
	}



}
