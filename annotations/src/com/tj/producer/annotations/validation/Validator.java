package com.tj.producer.annotations.validation;

import java.lang.reflect.Field;

public interface Validator {
	boolean isValid(Object object, Field f);
}
