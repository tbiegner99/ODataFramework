package com.tj.producer.validation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.tj.exceptions.ValidationException;
import com.tj.producer.annotations.validation.CustomValidator;
import com.tj.producer.annotations.validation.Number;
import com.tj.producer.annotations.validation.RegExp;
import com.tj.producer.annotations.validation.Required;
import com.tj.producer.annotations.validation.String;
import com.tj.producer.annotations.validation.Validator;
import com.tj.producer.util.ReflectionUtil;

public class BeanValidator {
	public static void validate(Object bean) {
		if (bean == null) {
			throw new IllegalArgumentException("Validation object cant be null");
		}
		Class<?> type = bean.getClass();
		while (type != null) {
			for (Field f : bean.getClass().getDeclaredFields()) {
				validateField(bean, f);
			}
			type = type.getSuperclass();
		}
	}

	private static void validateField(Object bean, Field f) {
		Object value = ReflectionUtil.getField(bean, f);
		if (f.isAnnotationPresent(Required.class)) {
			validateRequiredField(value, f);
		}
		if (f.isAnnotationPresent(String.class)) {
			validateString(value, f, f.getAnnotation(String.class));
		}
		if (f.isAnnotationPresent(Number.class)) {
			validateNumber(value, f, f.getAnnotation(Number.class));
		}
		if (f.isAnnotationPresent(RegExp.class)) {
			validateRegex(value, f, f.getAnnotation(RegExp.class));
		}
		if (f.isAnnotationPresent(CustomValidator.class)) {
			validateCustomValidator(value, f, f.getAnnotation(CustomValidator.class));
		}
	}

	private static void validateRegex(Object value, Field f, RegExp annotation) {
		if (value == null || !(value instanceof String)) {
			throw new ValidationException("Expecting a string type for property: " + f.getClass().getName());
		}
		try {
			if (!Pattern.matches(annotation.value(), value.toString())) {
				throw new ValidationException("Value does not match pattern for field: " + f.getName());
			}
		} catch (PatternSyntaxException e) {
			throw new ValidationException("Illegal matching pattern for field: " + f.getName());
		}

	}

	private static void validateRequiredField(Object value, Field f) {
		if (value == null) {
			throw new ValidationException("Field is required: " + f.getName());
		}
	}

	private static void validateString(Object value, Field f, String string) {
		if (value == null || !(value instanceof String)) {
			throw new ValidationException("Expecting a string type for property: " + f.getClass().getName());
		}
		int length = value.toString().length();
		if (length < string.minLength()) {
			throw new ValidationException("String is too short (" + length + "<" + string.minLength()
					+ ") for property: " + f.getClass().getName());
		}
		if (length > string.maxLength()) {
			throw new ValidationException("String is too short (" + length + ">" + string.maxLength()
					+ ") for property: " + f.getClass().getName());
		}
	}

	private static void validateNumber(Object value, Field f, Number annotation) {
		java.lang.Number number = null;
		if (value != null) {
			if (value instanceof String) {
				number = Double.parseDouble(value.toString());
			} else if (value instanceof Number) {
				number = (java.lang.Number) value;
			}
		}
		if (number == null) {
			throw new ValidationException("Expecting a number type or numeric string for property: "
					+ f.getClass().getName());
		}
		if (number.doubleValue() < annotation.minValue()) {
			throw new ValidationException("String is too short (" + number + "<" + annotation.minValue()
					+ ") for property: " + f.getClass().getName());
		}
		if (number.doubleValue() > annotation.maxValue()) {
			throw new ValidationException("String is too short (" + number + ">" + annotation.maxValue()
					+ ") for property: " + f.getClass().getName());
		}
	}

	private static void validateCustomValidator(Object value, Field f, CustomValidator customValidator) {

		try {
			Validator validator = ReflectionUtil.newDefaultInstance(customValidator.value());
			if (!validator.isValid(value, f)) {
				throw new ValidationException("Custom validation failed for property: " + f.getName() + " - "
						+ validator.getClass().getName() + " failed");
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new ValidationException("Could not instanitate class for validation: "
					+ customValidator.getClass().getName() + " - No public default constructor.");
		}

	}
}
