package com.tj.dao.filter;

import java.util.ArrayList;
import java.util.Collection;

import org.odata4j.expression.BoolMethodExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.EndsWithMethodCallExpression;
import org.odata4j.expression.StartsWithMethodCallExpression;
import org.odata4j.expression.SubstringOfMethodCallExpression;

public class FunctionFilter implements Filter {
	public static enum BoolFunction {
		STARTSWITH, ENDSWITH, SUBSTRINGOF
	}

	private Collection<Expression> args;
	private BoolFunction name;

	public FunctionFilter(BoolFunction name, Collection<Expression> args) {
		this.args = args;
		this.name = name;
	}

	public FunctionFilter(BoolFunction name, CommonExpression... args) {
		this.args = new ArrayList<Expression>();
		this.name = name;
		for (CommonExpression ce : args) {
			this.args.add(BasicExpression.fromOExpression(ce));
		}
	}

	public Collection<Expression> getArgs() {
		return args;
	}

	public BoolFunction getName() {
		return name;
	}

	public static Filter fromExpression(BoolMethodExpression e) {
		if (e instanceof EndsWithMethodCallExpression) {
			return new FunctionFilter(BoolFunction.ENDSWITH, e.getValue(), e.getTarget());
		} else if (e instanceof StartsWithMethodCallExpression) {
			return new FunctionFilter(BoolFunction.STARTSWITH, e.getValue(), e.getTarget());
		} else if (e instanceof SubstringOfMethodCallExpression) {
			return new FunctionFilter(BoolFunction.SUBSTRINGOF, e.getValue(), e.getTarget());
		}
		throw new RuntimeException("Bool function not found: " + e.getClass().getSimpleName());
	}
}
