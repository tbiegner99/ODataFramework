package com.tj.producer.annotations.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * A simple class level marker for an OData Entity class. This CAN be used to scan packages
 * for this annotation to define models. Other annotations may be used as well.
 * @author tbiegner
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ODataEntity {

}
