package com.tj.producer.annotations.validation;

public @interface Number {
	double minValue() default Double.MIN_VALUE;

	double maxValue() default Double.MAX_VALUE;
}
