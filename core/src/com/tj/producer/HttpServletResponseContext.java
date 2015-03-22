package com.tj.producer;

import java.io.IOException;
import java.sql.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseContext implements ResponseContext {

	private HttpServletResponse response;

	public HttpServletResponseContext(HttpServletResponse response) {
		this.response = response;
	}

	public void setDateHeader(String name, Date value) {
		response.setDateHeader(name, value.getTime());
	}

	public void redirect(String location) {
		try {
			response.sendRedirect(location);
		} catch (IOException e) {
			// throw runtime exception
		}
	}

	public void setHeader(String name, String value) {
		response.setHeader(name, value);
	}

	public void addCookie(String name, String value) {
		response.addCookie(new Cookie(name, value));
	}
}
