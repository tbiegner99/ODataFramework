package com.tj.producer.annotations.validation;

import java.lang.reflect.Field;

public class BeanValidator {
	public static boolean isValid(Object bean) {
		if (bean == null)
			return false;
		for (Field f : bean.getClass().getDeclaredFields()) {
			if (!validateField(bean, f))
				return false;
		}
		return true;
	}

	private static boolean validateField(Object bean, Field f) {

		return true;
	}

	private static boolean validateNumber() {
		return true;
	}

}
