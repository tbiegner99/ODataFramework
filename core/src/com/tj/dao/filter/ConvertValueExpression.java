package com.tj.dao.filter;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.odata4j.core.Guid;
import org.odata4j.core.UnsignedByte;
import org.odata4j.expression.BinaryLiteral;
import org.odata4j.expression.BooleanLiteral;
import org.odata4j.expression.ByteLiteral;
import org.odata4j.expression.CastExpression;
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

import com.tj.exceptions.IllegalRequestException;

public class ConvertValueExpression implements Expression {

	public static Expression fromOExpression(CastExpression exp) {
		if (!(exp.getExpression() instanceof LiteralExpression)) {
			throw new IllegalRequestException("Only literals may be cast");
		}
		try {
			switch (exp.getType().toLowerCase()) {
				case "binary":
					return convertToBinary((LiteralExpression) exp.getExpression());
				case "boolean":
					return convertToBoolean((LiteralExpression) exp.getExpression());
				case "byte":
					return convertToByte((LiteralExpression) exp.getExpression());
				case "datetime":
					return convertToDatetime((LiteralExpression) exp.getExpression());
				case "decimal":
					return convertToDecimal((LiteralExpression) exp.getExpression());
				case "double":
					return convertToDouble((LiteralExpression) exp.getExpression());
				case "single":
					return convertToSingle((LiteralExpression) exp.getExpression());
				case "guid":
					return convertToGuid((LiteralExpression) exp.getExpression());
				case "int16":
					return convertToShort((LiteralExpression) exp.getExpression());
				case "int32":
					return convertToInt((LiteralExpression) exp.getExpression());
				case "int64":
					return convertToLong((LiteralExpression) exp.getExpression());
				case "sbyte":
					return convertToSignedByte((LiteralExpression) exp.getExpression());
				case "string":
					return convertToString((LiteralExpression) exp.getExpression());
				case "time":
					return convertToTime((LiteralExpression) exp.getExpression());
				case "datetimeoffset":
					return convertToDatetimeOffset((LiteralExpression) exp.getExpression());
			}
		} catch (Exception e) {
			throw new IllegalRequestException("Value cannot be cast to " + exp.getType(), e);
		}
		throw new IllegalRequestException("Invalid type");
	}

	private static Expression convertToInt(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger intValue = new BigInteger(value);
			return new ValueExpression(intValue.intValue());
		} else if (exp instanceof BooleanLiteral) {
			Integer value = ((BooleanLiteral) exp).getValue() ? 1 : 0;
			return new ValueExpression(value);
		} else if (exp instanceof ByteLiteral) {
			int value = ((ByteLiteral) exp).getValue().intValue();
			return new ValueExpression(value);
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(value.intValue());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			Long value = ((DateTimeOffsetLiteral) exp).getValue().getMillis();
			return new ValueExpression(value.intValue());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.intValue());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value.intValue());
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value.intValue());
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value.intValue());
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(Integer.parseInt(value));
		} else if (exp instanceof TimeLiteral) {
			Integer value = ((TimeLiteral) exp).getValue().getMillisOfDay();
			return new ValueExpression(value);
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToLong(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger integer = new BigInteger(value);
			return new ValueExpression(integer.longValue());
		} else if (exp instanceof BooleanLiteral) {
			Long value = ((BooleanLiteral) exp).getValue() ? 1L : 0L;
			return new ValueExpression(value);
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value.longValue());
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(value);
		} else if (exp instanceof DateTimeOffsetLiteral) {
			DateTime value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression(value.getMillis());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.longValue());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value.longValue());
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value.longValue());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value.longValue());
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value.longValue());
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(Long.parseLong(value));
		} else if (exp instanceof TimeLiteral) {
			LocalTime value = ((TimeLiteral) exp).getValue();
			return new ValueExpression((long) value.getMillisOfDay());
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToSignedByte(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			return new ValueExpression((Byte) value[0]);
		} else if (exp instanceof BooleanLiteral) {
			byte value = ((BooleanLiteral) exp).getValue() ? (byte) 1 : (byte) 0;
			return new ValueExpression(value);
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			Long value = ((DateTimeOffsetLiteral) exp).getValue().getMillis();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(Byte.parseByte(value));
		} else if (exp instanceof TimeLiteral) {
			Integer value = ((TimeLiteral) exp).getValue().getMillisOfDay();
			return new ValueExpression(value.byteValue());
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToString(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			return new ValueExpression(new String(value, Charset.forName("UTF-8")));
		} else if (exp instanceof BooleanLiteral) {
			Boolean value = ((BooleanLiteral) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			Long value = ((DateTimeOffsetLiteral) exp).getValue().getMillis();
			return new ValueExpression(value.byteValue());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value.toString());
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof TimeLiteral) {
			Integer value = ((TimeLiteral) exp).getValue().getMillisOfDay();
			return new ValueExpression(value.toString());
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToTime(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger intVal = new BigInteger(value);
			return new ValueExpression(new LocalTime(intVal.longValue()));
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(new LocalTime(value.longValue()));
		} else if (exp instanceof DateTimeLiteral) {
			LocalDateTime value = ((DateTimeLiteral) exp).getValue();
			return new ValueExpression(value.toLocalTime());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			LocalTime value = ((DateTimeOffsetLiteral) exp).getValue().toLocalTime();
			return new ValueExpression(value);
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(new LocalTime(value.longValue()));
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(new LocalTime(value.longValue()));
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(new LocalTime(value.longValue()));
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(new LocalTime(value.longValue()));
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(new LocalTime(value.longValue()));
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(new LocalTime(value.longValue()));
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(LocalTime.parse(value));
		} else if (exp instanceof TimeLiteral) {
			LocalTime value = ((TimeLiteral) exp).getValue();
			return new ValueExpression(value);
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToDatetimeOffset(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger intVal = new BigInteger(value);
			return new ValueExpression(new DateTime(intVal.longValue()));
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(new DateTime(value.longValue()));
		} else if (exp instanceof DateTimeLiteral) {
			LocalDateTime value = ((DateTimeLiteral) exp).getValue();
			return new ValueExpression(value.toDateTime());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			DateTime value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(new DateTime(value.longValue()));
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(new DateTime(value.longValue()));
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(new DateTime(value.longValue()));
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(new DateTime(value.longValue()));
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(new DateTime(value.longValue()));
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(new DateTime(value.longValue()));
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(DateTime.parse(value));
		} else if (exp instanceof TimeLiteral) {
			DateTime value = ((TimeLiteral) exp).getValue().toDateTimeToday();
			return new ValueExpression(value);
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToShort(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger integer = new BigInteger(value);
			return new ValueExpression(integer.shortValue());
		} else if (exp instanceof BooleanLiteral) {
			Long value = ((BooleanLiteral) exp).getValue() ? 1L : 0L;
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			DateTime value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression((short) value.getMillis());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(Short.parseShort(value));
		} else if (exp instanceof TimeLiteral) {
			LocalTime value = ((TimeLiteral) exp).getValue();
			return new ValueExpression((short) value.getMillisOfDay());
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToSingle(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger integer = new BigInteger(value);
			return new ValueExpression(integer.floatValue());
		} else if (exp instanceof BooleanLiteral) {
			Long value = ((BooleanLiteral) exp).getValue() ? 1L : 0L;
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			DateTime value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression((float) value.getMillis());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value.floatValue());
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(Float.parseFloat(value));
		} else if (exp instanceof TimeLiteral) {
			LocalTime value = ((TimeLiteral) exp).getValue();
			return new ValueExpression((float) value.getMillisOfDay());
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToDouble(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger integer = new BigInteger(value);
			return new ValueExpression(integer.shortValue());
		} else if (exp instanceof BooleanLiteral) {
			Long value = ((BooleanLiteral) exp).getValue() ? 1L : 0L;
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			DateTime value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression((short) value.getMillis());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value.shortValue());
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(Short.parseShort(value));
		} else if (exp instanceof TimeLiteral) {
			LocalTime value = ((TimeLiteral) exp).getValue();
			return new ValueExpression((short) value.getMillisOfDay());
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToDecimal(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger integer = new BigInteger(value);
			return new ValueExpression(new BigDecimal(integer));
		} else if (exp instanceof BooleanLiteral) {
			Long value = ((BooleanLiteral) exp).getValue() ? 1L : 0L;
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value.longValue()));
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof DateTimeOffsetLiteral) {
			DateTime value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value.getMillis()));
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value));
		} else if (exp instanceof TimeLiteral) {
			LocalTime value = ((TimeLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value.getMillisOfDay()));
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToDatetime(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger intVal = new BigInteger(value);
			return new ValueExpression(new LocalDateTime(intVal.longValue()));
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(new LocalDateTime(value.longValue()));
		} else if (exp instanceof DateTimeLiteral) {
			LocalDateTime value = ((DateTimeLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DateTimeOffsetLiteral) {
			LocalDateTime value = ((DateTimeOffsetLiteral) exp).getValue().toLocalDateTime();
			return new ValueExpression(value);
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(new LocalDateTime(value.longValue()));
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(new LocalDateTime(value.longValue()));
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(new LocalDateTime(value.longValue()));
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(new LocalDateTime(value.longValue()));
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(new LocalDateTime(value.longValue()));
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(new LocalDateTime(value.longValue()));
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(LocalDateTime.parse(value));
		} else if (exp instanceof TimeLiteral) {
			LocalDateTime value = ((TimeLiteral) exp).getValue().toDateTimeToday().toLocalDateTime();
			return new ValueExpression(value);
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToByte(LiteralExpression exp) throws UnsupportedEncodingException {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			return new ValueExpression(value[0]);
		} else if (exp instanceof BooleanLiteral) {
			Integer value = ((BooleanLiteral) exp).getValue() ? 1 : 0;
			return new ValueExpression(new UnsignedByte(value));
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof DateTimeLiteral) {
			Long value = ((DateTimeLiteral) exp).getValue().toDateTime().getMillis();
			return new ValueExpression(new UnsignedByte(value.intValue()));
		} else if (exp instanceof DateTimeOffsetLiteral) {
			Long value = ((DateTimeOffsetLiteral) exp).getValue().getMillis();
			return new ValueExpression(new UnsignedByte(value.intValue()));
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(new UnsignedByte(value.intValue()));
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(new UnsignedByte(value.intValue()));
		} else if (exp instanceof GuidLiteral) {
			String value = ((GuidLiteral) exp).getValue().toString();
			return new ValueExpression(new UnsignedByte(value.getBytes("UTF-8")[0]));
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(new UnsignedByte(value.intValue()));
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(new UnsignedByte(value));
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(new UnsignedByte(value.intValue()));
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(new UnsignedByte(value.intValue()));
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(UnsignedByte.parseUnsignedByte(value));
		} else if (exp instanceof TimeLiteral) {
			Integer value = ((TimeLiteral) exp).getValue().getMillisOfDay();
			return new ValueExpression(new UnsignedByte(value));
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToGuid(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			return new ValueExpression(Guid.fromString(new String(value, Charset.forName("UTF-8"))));
		} else if (exp instanceof GuidLiteral) {
			return new ValueExpression(((GuidLiteral) exp).getValue());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(new ValueExpression(Guid.fromString(value)));
		}
		throw new IllegalRequestException("Type cannot be converted to guid.");
	}

	private static Expression convertToBoolean(LiteralExpression exp) {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			BigInteger intValue = new BigInteger(value);
			return new ValueExpression(intValue.compareTo(BigInteger.ZERO) != 0);
		} else if (exp instanceof BooleanLiteral) {
			boolean value = ((BooleanLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(value.compareTo(new UnsignedByte(0)) != 0);
		} else if (exp instanceof DateTimeLiteral) {
			LocalDateTime value = ((DateTimeLiteral) exp).getValue();
			return new ValueExpression(value.toDateTime().getMillis() != 0);
		} else if (exp instanceof DateTimeOffsetLiteral) {
			Long value = ((DateTimeOffsetLiteral) exp).getValue().getMillis();
			return new ValueExpression(value != 0);
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.compareTo(BigDecimal.ZERO) != 0);
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(value != 0);
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(value != 0);
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(value != 0);
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(false);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(value != 0);
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(value != 0);
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(Boolean.parseBoolean(value));
		} else if (exp instanceof TimeLiteral) {
			Integer value = ((TimeLiteral) exp).getValue().getMillisOfDay();
			return new ValueExpression(value != 0);
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

	private static Expression convertToBinary(LiteralExpression exp) throws UnsupportedEncodingException {
		if (exp instanceof BinaryLiteral) {
			byte[] value = ((BinaryLiteral) exp).getValue();
			return new ValueExpression(value);
		} else if (exp instanceof BooleanLiteral) {
			boolean value = ((BooleanLiteral) exp).getValue();
			return new ValueExpression(new byte[] { (byte) (value ? 1 : 0) });
		} else if (exp instanceof ByteLiteral) {
			UnsignedByte value = ((ByteLiteral) exp).getValue();
			return new ValueExpression(new byte[] { value.byteValue() });
		} else if (exp instanceof DateTimeLiteral) {
			LocalDateTime value = ((DateTimeLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value.toDateTime().getMillis()).toBigInteger().toByteArray());
		} else if (exp instanceof DateTimeOffsetLiteral) {
			DateTime value = ((DateTimeOffsetLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value.getMillis()).toBigInteger().toByteArray());
		} else if (exp instanceof DecimalLiteral) {
			BigDecimal value = ((DecimalLiteral) exp).getValue();
			return new ValueExpression(value.toBigInteger().toByteArray());
		} else if (exp instanceof DoubleLiteral) {
			Double value = ((DoubleLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value).toBigInteger().toByteArray());
		} else if (exp instanceof GuidLiteral) {
			String value = ((GuidLiteral) exp).getValue().toString();
			return new ValueExpression(value.getBytes("UTF-8"));
		} else if (exp instanceof Int64Literal) {
			Long value = ((Int64Literal) exp).getValue();
			return new ValueExpression(new BigDecimal(value).toBigInteger().toByteArray());
		} else if (exp instanceof IntegralLiteral) {
			Integer value = ((IntegralLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value).toBigInteger().toByteArray());
		} else if (exp instanceof NullLiteral) {
			return new ValueExpression(null);
		} else if (exp instanceof SByteLiteral) {
			Byte value = ((SByteLiteral) exp).getValue();
			return new ValueExpression(new byte[] { value });
		} else if (exp instanceof SingleLiteral) {
			Float value = ((SingleLiteral) exp).getValue();
			return new ValueExpression(new BigDecimal(value).toBigInteger().toByteArray());
		} else if (exp instanceof StringLiteral) {
			String value = ((StringLiteral) exp).getValue();
			return new ValueExpression(value.getBytes("UTF-8"));
		} else if (exp instanceof TimeLiteral) {
			Integer value = ((TimeLiteral) exp).getValue().getMillisOfDay();
			return new ValueExpression(value);
		}
		throw new RuntimeException("Expression type not supported: " + exp.getClass().getSimpleName());
	}

}
