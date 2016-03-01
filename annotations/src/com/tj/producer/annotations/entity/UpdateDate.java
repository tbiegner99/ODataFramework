package com.tj.producer.annotations.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Marks a field as supplying a create date for the object.
 * On update, this will be set to the current Date.
 * 
 *
 * @author tbiegner
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface UpdateDate {
}
