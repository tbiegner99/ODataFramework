package com.tj.producer.annotations.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/***
 * Marks a field or method as providing content for a media entity.
 * if present the entity will be a media entity. A content type annotation may also
 * be  supplied on a different field or method specifying that the content of that property or
 * method should be the content type.
 *
 * @author tbiegner
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,ElementType.FIELD })
public @interface Binary {
	String contentType() default "text/plain";
}
