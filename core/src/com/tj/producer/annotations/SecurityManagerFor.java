package com.tj.producer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the class that a security manager is for during package scanning. If the class implements the security manager interface
 * directly, this is not necessary. However, if it extends a security manager class, due to type erasure, this becomes important,
 * or the class will be skipped. It is important the class supplied is a subclass of the generic class implemented by the security manager interface,
 * or the security manager will be vulnerable to class cast exceptions.
 * @author tbiegner
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SecurityManagerFor {
	Class<?> type();
}
