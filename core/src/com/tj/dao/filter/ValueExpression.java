package com.tj.dao.filter;

import org.odata4j.expression.BinaryLiteral;
import org.odata4j.expression.BooleanLiteral;
import org.odata4j.expression.ByteLiteral;
import org.odata4j.expression.DateTimeLiteral;
import org.odata4j.expression.DateTimeOffsetLiteral;
import org.odata4j.expression.DecimalLiteral;
import org.odata4j.expression.DoubleLiteral;
import org.odata4j.expression.GuidLiteral;
import org.odata4j.expression.Int64Literal;
import org.odata4j.expression.IntegralLiteral;
import org.odata4j.expression.LiteralExpression;
import org.odata4j.expression.NullLiteral;
import org.odata4j.expression.SByteLiteral;
import org.odata4j.expression.SingleLiteral;
import org.odata4j.expression.StringLiteral;
import org.odata4j.expression.TimeLiteral;

public class ValueExpression implements Expression {

	private Object value;

	public ValueExpression(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public static Expression fromExpresion(LiteralExpression exp) {
		Object value;
		if (exp instanceof BinaryLiteral) {
			value = ((BinaryLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof BooleanLiteral) {
			value = ((BooleanLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof ByteLiteral) {
			value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DateTimeLiteral) {
			value = ((DateTimeLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DateTimeOffsetLiteral) {
			value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DecimalLiteral) {
			value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DoubleLiteral) {
			value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof GuidLiteral) {
			value = ((GuidLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof Int64Literal) {
			value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof IntegralLiteral) {
			value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof SingleLiteral) {
			value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof StringLiteral) {
			value = ((StringLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof TimeLiteral) {
			value = ((TimeLiteral) exp).getValue();
			return new ValueExpression(value);
		}
		throw new RuntimeException("Unsopported expression literal: " + exp.getClass().getSimpleName());
	}

}
