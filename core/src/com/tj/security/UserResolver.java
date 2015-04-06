package com.tj.security;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import com.tj.exceptions.NoLoginException;

public interface UserResolver<T extends User> {

	public T getUser(HttpServletRequest request, SecurityContext context) throws NoLoginException;
}
