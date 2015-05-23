package com.tj.producer.annotations.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an id field in an odata entity. The javax.persistence.Id annotation may also be used.
 * This should be used in the absence of javax.persistence,
 * @author tbiegner
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Key {

}
