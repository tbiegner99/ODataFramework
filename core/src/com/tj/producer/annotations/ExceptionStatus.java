package com.tj.producer.annotations;

import org.springframework.http.HttpStatus;

public @interface ExceptionStatus {
	HttpStatus[] code();

	Class<? extends Exception> exception();
}
