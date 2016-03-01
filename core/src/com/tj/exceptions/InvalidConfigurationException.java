package com.tj.exceptions;

public class InvalidConfigurationException extends RuntimeException {

	private static final long serialVersionUID = -8915342573190934332L;

	public InvalidConfigurationException(String string, Exception e) {
		super(string, e);
	}

}
