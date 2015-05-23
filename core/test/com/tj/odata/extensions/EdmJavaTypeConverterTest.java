package com.tj.odata.extensions;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.odata4j.core.Guid;
import org.odata4j.core.UnsignedByte;
import org.odata4j.edm.EdmSimpleType;

import com.tj.exceptions.IllegalTypeException;
import com.tj.producer.util.ReflectionUtil;


public class EdmJavaTypeConverterTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertNumber() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?>[] target={Integer.class,UnsignedByte.class,Byte.class,Double.class,Long.class,Short.class,Float.class,BigDecimal.class,BigInteger.class};
		for(Class<?> subject : target) {
			for(Class<?> result :target) {
				Number n;
				Object value;
				if(subject==UnsignedByte.class) {
					value=subject.getConstructor(int.class).newInstance(5);
				} else {
					value=subject.getConstructor(String.class).newInstance("5");
				}
				n=EdmJavaTypeConverter.convertNumber(value , (Class<Number>)result);
				Number num=new Number() {
					@Override
					public int intValue() {return 0;}
					@Override
					public long longValue() {return 0;}
					@Override
					public float floatValue() {return 0;}
					@Override
					public double doubleValue() {return 0;}
				};
				try{
					EdmJavaTypeConverter.convertNumber(value, num.getClass());
				} catch(IllegalTypeException e){}
				try{
					EdmJavaTypeConverter.convertNumber("Not a number", (Class<Number>)result);
					fail("Expected exception");
				} catch(IllegalTypeException e){}
				Assert.assertEquals(n.getClass(), result);
				Assert.assertEquals(n.doubleValue(), 5.0,.0001);
			}
		}

	}

	@Test
	public void testConvertToClass() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		for(EdmSimpleType<?> t : EdmSimpleType.ALL) {

				Class<?> clazz=t.getCanonicalJavaType();
				Set<Class<?>> clazzes=new HashSet<>(t.getJavaTypes());
				clazzes.add(clazz);
				for(Class<?> target : clazzes) {
					Object value;
					if(clazz==UUID.class) {
						value=UUID.randomUUID();
					} else if(clazz==Guid.class) {
						value=Guid.randomGuid();
					} else if(ReflectionUtil.isPrimitiveOrWrapper(clazz)) {
						value=clazz.getConstructor(String.class).newInstance("40");
					} else if (clazz==UnsignedByte.class) {
						value=new UnsignedByte(25);
					}else if(clazz==byte[].class) {
						value=new byte[]{(byte) 0xFF,(byte) 0xFF};
					}else {
						value=clazz.newInstance();

					}

					EdmJavaTypeConverter.convertToClass(t, value, target);
				}
		}
	}

}
