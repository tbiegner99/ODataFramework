package com.tj.dao.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.odata4j.expression.CeilingMethodCallExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.ConcatMethodCallExpression;
import org.odata4j.expression.DayMethodCallExpression;
import org.odata4j.expression.FloorMethodCallExpression;
import org.odata4j.expression.HourMethodCallExpression;
import org.odata4j.expression.IndexOfMethodCallExpression;
import org.odata4j.expression.LengthMethodCallExpression;
import org.odata4j.expression.MethodCallExpression;
import org.odata4j.expression.MinuteMethodCallExpression;
import org.odata4j.expression.MonthMethodCallExpression;
import org.odata4j.expression.ReplaceMethodCallExpression;
import org.odata4j.expression.RoundMethodCallExpression;
import org.odata4j.expression.SecondMethodCallExpression;
import org.odata4j.expression.SubstringMethodCallExpression;
import org.odata4j.expression.ToLowerMethodCallExpression;
import org.odata4j.expression.ToUpperMethodCallExpression;
import org.odata4j.expression.TrimMethodCallExpression;
import org.odata4j.expression.YearMethodCallExpression;

public class MethodExpression implements Expression {

	public static enum Function {
		CEIL, CONCAT, DAY, FLOOR, HOUR, INDEXOF, LENGTH, MINUTE, MONTH, REPLACE, ROUND, SECOND, SUBSTRING, TOLOWER, TOUPPER, TRIM, YEAR
	}

	private Function name;
	private Collection<Expression> arguments;

	public MethodExpression(Function name, Expression... args) {
		this.name = name;
		arguments = Arrays.asList(args);
	}

	public MethodExpression(Function name, CommonExpression... args) {
		this.name = name;
		arguments = new ArrayList<Expression>();
		for (CommonExpression com : args) {
			arguments.add(BasicExpression.fromOExpression(com));
		}
	}

	public Function getName() {
		return name;
	}

	public Collection<Expression> getArguments() {
		return arguments;
	}

	public static MethodExpression fromOExpression(MethodCallExpression exp) {
		if (exp instanceof CeilingMethodCallExpression) {
			CeilingMethodCallExpression casted = ((CeilingMethodCallExpression) exp);
			return new MethodExpression(Function.CEIL, casted.getTarget());

		} else if (exp instanceof ConcatMethodCallExpression) {
			ConcatMethodCallExpression casted = ((ConcatMethodCallExpression) exp);
			return new MethodExpression(Function.CONCAT, casted.getLHS(), casted.getRHS());

		} else if (exp instanceof DayMethodCallExpression) {
			ConcatMethodCallExpression casted = ((ConcatMethodCallExpression) exp);
			return new MethodExpression(Function.DAY, casted.getLHS(), casted.getRHS());

		} else if (exp instanceof FloorMethodCallExpression) {
			FloorMethodCallExpression casted = ((FloorMethodCallExpression) exp);
			return new MethodExpression(Function.FLOOR, casted.getTarget());

		} else if (exp instanceof HourMethodCallExpression) {
			HourMethodCallExpression casted = ((HourMethodCallExpression) exp);
			return new MethodExpression(Function.HOUR, casted.getTarget());

		} else if (exp instanceof IndexOfMethodCallExpression) {
			IndexOfMethodCallExpression casted = ((IndexOfMethodCallExpression) exp);
			return new MethodExpression(Function.INDEXOF, casted.getTarget(), casted.getValue());

		} else if (exp instanceof LengthMethodCallExpression) {
			LengthMethodCallExpression casted = ((LengthMethodCallExpression) exp);
			return new MethodExpression(Function.LENGTH, casted.getTarget());

		} else if (exp instanceof MinuteMethodCallExpression) {
			MinuteMethodCallExpression casted = ((MinuteMethodCallExpression) exp);
			return new MethodExpression(Function.MINUTE, casted.getTarget());

		} else if (exp instanceof MonthMethodCallExpression) {
			MonthMethodCallExpression casted = ((MonthMethodCallExpression) exp);
			return new MethodExpression(Function.MONTH, casted.getTarget());

		} else if (exp instanceof ReplaceMethodCallExpression) {
			ReplaceMethodCallExpression casted = ((ReplaceMethodCallExpression) exp);
			return new MethodExpression(Function.REPLACE, casted.getTarget(), casted.getFind(), casted.getReplace());

		} else if (exp instanceof RoundMethodCallExpression) {
			RoundMethodCallExpression casted = ((RoundMethodCallExpression) exp);
			return new MethodExpression(Function.ROUND, casted.getTarget());

		} else if (exp instanceof SecondMethodCallExpression) {
			SecondMethodCallExpression casted = ((SecondMethodCallExpression) exp);
			return new MethodExpression(Function.SECOND, casted.getTarget());

		} else if (exp instanceof SubstringMethodCallExpression) {
			SubstringMethodCallExpression casted = ((SubstringMethodCallExpression) exp);
			return new MethodExpression(Function.SUBSTRING, casted.getTarget());

		} else if (exp instanceof ToLowerMethodCallExpression) {
			ToLowerMethodCallExpression casted = ((ToLowerMethodCallExpression) exp);
			return new MethodExpression(Function.TOLOWER, casted.getTarget());

		} else if (exp instanceof ToUpperMethodCallExpression) {
			ToUpperMethodCallExpression casted = ((ToUpperMethodCallExpression) exp);
			return new MethodExpression(Function.TOUPPER, casted.getTarget());

		} else if (exp instanceof TrimMethodCallExpression) {
			TrimMethodCallExpression casted = ((TrimMethodCallExpression) exp);
			return new MethodExpression(Function.TRIM, casted.getTarget());

		} else if (exp instanceof YearMethodCallExpression) {
			YearMethodCallExpression casted = ((YearMethodCallExpression) exp);
			return new MethodExpression(Function.YEAR, casted.getTarget());
		}
		throw new RuntimeException("Function not supported: " + exp.getClass().getSimpleName());
	}
}
