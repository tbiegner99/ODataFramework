package com.tj.exceptions;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.odata4j.exceptions.ODataProducerException;


public class GenericOdataProducerException extends ODataProducerException{
	private static final long serialVersionUID = 9095667028702364438L;
	private StatusType type;

	public GenericOdataProducerException(Throwable cause) {
		this(cause.getLocalizedMessage(), cause);
	}

	public GenericOdataProducerException(String message, Throwable cause) {
		super(message, cause);
		type=Status.INTERNAL_SERVER_ERROR;
	}

	public GenericOdataProducerException(StatusType status,Throwable cause) {
		this(status,cause.getLocalizedMessage(), cause);
	}

	public GenericOdataProducerException(StatusType status, String message, Throwable cause) {
		super(message, cause);
		type=status;
	}

	@Override
	public StatusType getHttpStatus() {
		return type;
	}
}
