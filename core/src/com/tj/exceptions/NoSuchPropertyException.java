package com.tj.exceptions;


public class NoSuchPropertyException extends PropertyError {
	private static final long serialVersionUID = 1086016285477670546L;

	public NoSuchPropertyException() {
		super();
	}

	public NoSuchPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchPropertyException(String message) {
		super(message);
	}

}
