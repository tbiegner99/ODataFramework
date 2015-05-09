package com.tj.producer;

import javax.servlet.http.HttpServletRequest;

import org.odata4j.producer.ODataProducer;
import org.springframework.transaction.annotation.Transactional;

import com.tj.security.user.User;
import com.tj.security.user.UserResolver;

/***
 * Adds a security context to the OData4jODataProducer
 * @author tbiegner
 *
 */
@Transactional
public interface UserAwareODataProducer extends ODataProducer {
	UserResolver<?> getUserResolver();
	User resolveUser(HttpServletRequest request);
}
