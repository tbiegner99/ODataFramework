package com.tj.odata.extensions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.odata4j.core.Guid;
import org.odata4j.edm.EdmSimpleType;

import com.tj.exceptions.IllegalOperationException;
import com.tj.exceptions.IllegalTypeException;

public class EdmJavaTypeConverter {
	public static <T extends Number> T convertNumber(Number i,Class<T> target) {
		try {
			if(target == BigDecimal.class) {
				return (T) new BigDecimal(i.toString());
			} else if(target == BigInteger.class) {
				return (T) new BigInteger(i.toString());
			} else if(target==Byte.class || target==byte.class) {
				return (T) new Byte(i.toString());
			} else if(target==Double.class || target==double.class) {
				return (T) new Integer(i.toString());
			} else if(target==Float.class || target==float.class) {
				return (T) new Integer(i.toString());
			} else if(target==Integer.class || target==int.class) {
				return (T) new Integer(i.toString());
			} else if(target==Long.class || target==long.class) {
				return (T) new Long(i.toString());
			} else if(target==Short.class || target==short.class) {
				return (T) new Short(i.toString());
			}
			throw new NumberFormatException("Number type not supported: "+target.getSimpleName());
		} catch(NumberFormatException e) {
			throw new IllegalTypeException(e.getMessage(),e);
		}
	}
	public static Object convertToClass(EdmSimpleType<?> type, Object value, Class<?> target) {
		Class<?> source = value.getClass();
		if (source == target) {
			return value;
		}
		if (!type.getJavaTypes().contains(target)) {
			throw new IllegalOperationException("The object of type " + value.getClass().getName()
					+ " cannot be converted to " + target.getName());
		}
		if (source == byte[].class) {
			return convertBinary((byte[]) value);
		} else if (source == Boolean.class) {
			return convertBoolean((Boolean) value);
		} else if (source == LocalDateTime.class) {
			return convertDateTime((LocalDateTime) value, target);
		} else if (source == Double.class) {
			return convertDouble((Double) value);
		} else if (source == Guid.class) {
			return convertUid((Guid) value);
		} else if (source == Short.class) {
			return convertShort((Short) value);
		} else if (source == Integer.class) {
			return convertInteger((Integer) value);
		} else if (source == Long.class) {
			return convertLong((Long) value);
		} else if (source == Byte.class) {
			return convertByte((Byte) value);
		} else if (source == Float.class) {
			return convertFloat((Float) value);
		} else if (source == String.class) {
			return convertString((String) value, target);
		} else if (source == LocalTime.class) {
			return convertTime((LocalTime) value);
		}
		throw new IllegalOperationException("");
	}

	private static Object convertBinary(byte[] obj) {
		Byte[] ret = new Byte[obj.length];
		for (int i = 0; i < obj.length; i++) {
			ret[i] = new Byte(obj[i]);
		}
		return ret;
	}

	private static Object convertBoolean(Boolean bool) {
		return bool.booleanValue();
	}

	private static Object convertDateTime(LocalDateTime time, Class<?> target) {
		if (target == Instant.class) {
			return new Instant(time.toDate().getTime());
		} else if (target == Date.class) {
			return time.toDate();
		} else if (Calendar.class.isAssignableFrom(target)) {
			Calendar c;
			try {
				c = (Calendar) target.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Unable to instantiate calendar of type: " + target.getName(), e);
			}
			c.setTime(time.toDate());
			return c;
		} else if (target == Timestamp.class) {
			return new Timestamp(time.toDate().getTime());
		} else if (target == java.sql.Date.class) {
			return new java.sql.Date(time.toDate().getTime());
		}
		return time.toDate();
	}

	private static Object convertDouble(Double d) {
		return d.doubleValue();
	}

	private static Object convertShort(Short s) {
		return s.doubleValue();
	}

	private static Object convertInteger(Integer i) {
		return i.intValue();
	}

	private static Object convertLong(Long l) {
		return l.longValue();
	}

	private static Object convertByte(Byte b) {
		return b.byteValue();
	}

	private static Object convertString(String s, Class<?> target) {
		char c = (s.isEmpty() ? 0 : s.charAt(0));
		if (target == char.class) {
			return c;
		} else if (target == Character.class) {
			return new Character(c);
		}
		return s;
	}

	private static Object convertFloat(Float f) {
		return f.floatValue();
	}

	private static Object convertTime(LocalTime time) {
		return new Time(time.toDateTimeToday().getMillis());
	}

	private static Object convertUid(Guid uid) {
		return UUID.fromString(uid.toString());
	}
}
