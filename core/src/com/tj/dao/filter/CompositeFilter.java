package com.tj.dao.filter;

public class CompositeFilter implements Filter {
	private Filter lhs;
	private Filter rhs;

	public CompositeFilter(Filter lhs, Filter rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public Filter getLhs() {
		return lhs;
	}

	public Filter getRhs() {
		return rhs;
	}

}
