package com.tj.dao.filter;

public class LiteralFilter implements Filter {

	private boolean value;

	public LiteralFilter(boolean value) {
		this.value = value;
	}

	public boolean isValue() {
		return value;
	}

}
