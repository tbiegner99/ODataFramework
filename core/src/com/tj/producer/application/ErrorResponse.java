package com.tj.producer.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.odata4j.core.OError;

@XmlRootElement
public class ErrorResponse implements OError {
	@XmlElement
	private String code;
	@XmlElement
	private String message;
	@XmlElement
	private String innerError;

	public ErrorResponse(Throwable error) {
		code = error.getClass().getName();
		if (error.getCause() != null) {
			innerError = error.getCause().getLocalizedMessage();
		}
		message = error.getLocalizedMessage();
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getInnerError() {
		return innerError;
	}

}
