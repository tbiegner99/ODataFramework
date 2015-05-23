package com.tj.dao.filter;

import org.odata4j.expression.AddExpression;
import org.odata4j.expression.CastExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.DivExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.LiteralExpression;
import org.odata4j.expression.MethodCallExpression;
import org.odata4j.expression.ModExpression;
import org.odata4j.expression.MulExpression;
import org.odata4j.expression.ParenExpression;
import org.odata4j.expression.SubExpression;

public class BasicExpression implements Expression {
	private Expression lhs;
	private Expression rhs;
	private Operator op;

	public static enum Operator {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD, POWER
	}

	public BasicExpression(Expression lhs2, Operator op, Expression rhs2) {
		lhs = lhs2;
		rhs = rhs2;
		this.op = op;
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

	public Operator getOp() {
		return op;
	}

	public void setOp(Operator op) {
		this.op = op;
	}

	public static Expression fromOExpression(CommonExpression exp) {
		if (exp instanceof AddExpression) {
			Expression lhs = fromOExpression(((AddExpression) exp).getLHS());
			Expression rhs = fromOExpression(((AddExpression) exp).getRHS());
			return new BasicExpression(lhs, Operator.ADD, rhs);
		} else if (exp instanceof MulExpression) {
			Expression lhs = fromOExpression(((MulExpression) exp).getLHS());
			Expression rhs = fromOExpression(((MulExpression) exp).getRHS());
			return new BasicExpression(lhs, Operator.MULTIPLY, rhs);
		} else if (exp instanceof SubExpression) {
			Expression lhs = fromOExpression(((SubExpression) exp).getLHS());
			Expression rhs = fromOExpression(((SubExpression) exp).getRHS());
			return new BasicExpression(lhs, Operator.SUBTRACT, rhs);
		} else if (exp instanceof DivExpression) {
			Expression lhs = fromOExpression(((DivExpression) exp).getLHS());
			Expression rhs = fromOExpression(((DivExpression) exp).getRHS());
			return new BasicExpression(lhs, Operator.DIVIDE, rhs);
		} else if (exp instanceof ModExpression) {
			Expression lhs = fromOExpression(((ModExpression) exp).getLHS());
			Expression rhs = fromOExpression(((ModExpression) exp).getRHS());
			return new BasicExpression(lhs, Operator.MOD, rhs);
		} else if (exp instanceof LiteralExpression) {
			return ValueExpression.fromExpresion((LiteralExpression) exp);
		} else if (exp instanceof EntitySimpleProperty) {
			return new PropertyExpression(((EntitySimpleProperty) exp).getPropertyName());
		} else if (exp instanceof MethodCallExpression) {
			return MethodExpression.fromOExpression((MethodCallExpression) exp);
		} else if (exp instanceof CastExpression) {
			return ConvertValueExpression.fromOExpression((CastExpression) exp);
		} else if (exp instanceof ParenExpression) {
			return BasicExpression.fromOExpression(((ParenExpression) exp).getExpression());
		}
		// TODO: cast expression
		throw new RuntimeException("Type not supported expression:" + exp.getClass().getName());
	}
}
