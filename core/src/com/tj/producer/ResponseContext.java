package com.tj.producer;

import java.sql.Date;

public interface ResponseContext {

	public void setDateHeader(String name, Date value);

	public void redirect(String location);

	public void setHeader(String name, String value);

	public void addCookie(String name, String value);

	/**
	 * A placeholder response context. does nothing
	 * 
	 * @author tbiegner
	 *
	 */
	public static class DefaultResponseContext implements ResponseContext {

		public DefaultResponseContext() {
		}

		@Override
		public void setDateHeader(String name, Date value) {
		}

		@Override
		public void redirect(String location) {
		}

		@Override
		public void setHeader(String name, String value) {
		}

		@Override
		public void addCookie(String name, String value) {
		}

	}
}
