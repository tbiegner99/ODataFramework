package com.tj.producer.annotations.validation;

public @interface CustomValidator {
	Class<? extends Validator> value();
}
