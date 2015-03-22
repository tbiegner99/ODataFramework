package com.tj.dao.filter;

import java.util.Collection;
import java.util.Iterator;

import org.odata4j.expression.AggregateAllFunction;
import org.odata4j.expression.AggregateAnyFunction;
import org.odata4j.expression.AndExpression;
import org.odata4j.expression.BinaryCommonExpression;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.BoolMethodExpression;
import org.odata4j.expression.BoolParenExpression;
import org.odata4j.expression.BooleanLiteral;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.EqExpression;
import org.odata4j.expression.GeExpression;
import org.odata4j.expression.GtExpression;
import org.odata4j.expression.LeExpression;
import org.odata4j.expression.LtExpression;
import org.odata4j.expression.NeExpression;
import org.odata4j.expression.NotExpression;
import org.odata4j.expression.NullLiteral;
import org.odata4j.expression.OrExpression;
import org.odata4j.producer.resources.OptionsQueryParser;

public class BasicFilter implements Filter {
	private Expression lhs;
	private Expression rhs;
	private Comparison compare;

	public static enum Comparison {
		EQUALS, GT, LT, LE, GE, NOT_EQUALS, NULL, NOT_NULL
	}

	public BasicFilter(Expression lhs, Comparison comp, Expression rhs) {
		this.lhs = lhs;
		compare = comp;
		this.rhs = rhs;
	}

	public Expression getLhs() {
		return lhs;
	}

	public void setLhs(Expression lhs) {
		this.lhs = lhs;
	}

	public Expression getRhs() {
		return rhs;
	}

	public void setRhs(Expression rhs) {
		this.rhs = rhs;
	}

	public Comparison getCompare() {
		return compare;
	}

	public void setCompare(Comparison compare) {
		this.compare = compare;
	}

	public static Filter fromExpression(CommonExpression e) {
		if (e instanceof AndExpression) {
			return AndFilter.fromExpression((AndExpression) e);
		} else if (e instanceof OrExpression) {
			return OrFilter.fromExpression((OrExpression) e);
		} else if (e instanceof NotExpression) {
			return NotFilter.fromExpression((NotExpression) e);
		} else if (e instanceof BooleanLiteral) {
			return new LiteralFilter(((BooleanLiteral) e).getValue());
		} else if (e instanceof BoolParenExpression) {
			return fromExpression(((BoolParenExpression) e).getExpression());
		} else if (e instanceof BoolMethodExpression) {
			return FunctionFilter.fromExpression((BoolMethodExpression) e);
		} else if (e instanceof AggregateAllFunction) {
			return AllFilter.fromFilter((AggregateAllFunction) e);
		} else if (e instanceof AggregateAnyFunction) {
			return AnyFilter.fromFilter((AggregateAnyFunction) e);
		} else {
			Expression lhs = BasicExpression.fromOExpression(((BinaryCommonExpression) e).getLHS());
			if (((BinaryCommonExpression) e).getRHS() instanceof NullLiteral) {
				if (e instanceof EqExpression) {
					return new BasicFilter(lhs, Comparison.NULL, null);
				} else {
					return new BasicFilter(lhs, Comparison.NOT_NULL, null);
				}
			}
			Expression rhs = BasicExpression.fromOExpression(((BinaryCommonExpression) e).getRHS());
			if (e instanceof EqExpression) {
				return new BasicFilter(lhs, Comparison.EQUALS, rhs);
			} else if (e instanceof GeExpression) {
				return new BasicFilter(lhs, Comparison.GE, rhs);
			} else if (e instanceof GtExpression) {
				return new BasicFilter(lhs, Comparison.GT, rhs);
			} else if (e instanceof LtExpression) {
				return new BasicFilter(lhs, Comparison.LT, rhs);
			} else if (e instanceof LeExpression) {
				return new BasicFilter(lhs, Comparison.LE, rhs);
			} else if (e instanceof NeExpression) {
				return new BasicFilter(lhs, Comparison.NOT_EQUALS, rhs);
			}
		}
		// TODO: ANY,ALL, functions
		throw new RuntimeException("Not Impolemented: " + e.getClass().getName());
	}

	public static Filter joinFilters(Collection<Filter> filters) {
		if(filters==null || filters.isEmpty()) {return null;}
		Iterator<Filter> it=filters.iterator();
		Filter ret=it.next();
		while(it.hasNext()) {
			ret=new AndFilter(ret,it.next());
		}
		return ret;
	}
	public static Filter fromExpression(String filterExp) {
		BoolCommonExpression exp=OptionsQueryParser.parseFilter(filterExp);
		return BasicFilter.fromExpression(exp);
	}

}
