package com.tj.exceptions;


public class PropertyError extends RuntimeException {

	private static final long serialVersionUID = -8269171414116721911L;
	public PropertyError(){}

	public PropertyError(String message) {
		super(message);
	}
	public PropertyError(String message,Throwable cause) {
		super(message,cause);
	}

}
