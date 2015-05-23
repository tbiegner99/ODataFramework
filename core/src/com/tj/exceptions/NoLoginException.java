package com.tj.exceptions;


public class NoLoginException extends Exception {

	private static final long serialVersionUID = 1907701143478635740L;

	public NoLoginException(String message, Throwable e) {
		super(message, e);
	}

	public NoLoginException(String message) {
		super(message);
	}

	public NoLoginException() {
	}

}
