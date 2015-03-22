package com.tj.producer.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.tj.exceptions.NoSuchPropertyException;
import com.tj.exceptions.PropertyError;

public class ReflectionUtil {
	public static Collection<Class<?>> getMarkedClassesInPackage(String packageName, Class<? extends Annotation> marker) {
		Reflections reflect = createReflectionsObject(packageName);
		return reflect.getTypesAnnotatedWith(marker);
	}

	private static Reflections createReflectionsObject(String packageName) {
		return new Reflections(packageName);
	}
	@SafeVarargs
	public static Collection<Class<?>> getMarkedClassesInPackage(Collection<String> packageName,
			Iterable<Class<? extends Annotation>> markers, Class<?>... ignoredClasses) {
		Set<Class<?>> ignoreIndex = new HashSet<>(Arrays.asList(ignoredClasses));
		Collection<Class<?>> classes = new ArrayList<Class<?>>();
		for (String s : packageName) {
			for (Class<? extends Annotation> marker : markers) {
				for (Class<?> candidate : getMarkedClassesInPackage(s, marker)) {
					if (!ignoreIndex.contains(candidate)) {
						classes.add(candidate);
					}
				}
			}
		}
		return classes;
	}

	public static Set<Class<?>> getSubTypesInPackages(Collection<String> packages, Class<?> superType,
			Class<?>... ignore) {
		return getSubTypesInPackages(packages, superType, Arrays.asList(ignore));
	}

	public static Set<Class<?>> getSubTypesInPackages(Collection<String> packages, Class<?> superType,
			Collection<Class<?>> ignore) {
		Set<Class<?>> ignoreSet = new HashSet<>();
		if (ignore != null) {
			ignoreSet.addAll(ignore);
		}
		Set<Class<?>> ret = new HashSet<Class<?>>();
		for (String packageName : packages) {
			Reflections reflect = createReflectionsObject(packageName);
			for (Class<?> candidate : reflect.getSubTypesOf(superType)) {
				if (!ignoreSet.contains(candidate)) {
					ret.add(candidate);
				}
			}
		}
		return ret;
	}

	public static void invokeSetter(Object obj, PropertyDescriptor pd, Object setterValue) {
		if (pd.getWriteMethod() != null) {
			try {
				pd.getWriteMethod().invoke(obj, setterValue);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("No setter for property: " + pd.getName() + " in type "
				+ obj.getClass().getCanonicalName());
	}
	public static Object getValueDirectFromField(Object o,String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		//work up the heirarchy chain
		Class<?> clazz=o.getClass();
		while(clazz!=null) {
			try{
				Field f = clazz.getDeclaredField(fieldName);
				f.setAccessible(true);
				return f.get(o);
			}catch(NoSuchFieldException e) {
				clazz=clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException("Field "+fieldName+" does not exist in "+o.getClass().getCanonicalName()+" or its supertypes.");
	}
	public static Object getFieldValue(Object o, String fieldName) {
		// Field f;
		try {
			PropertyDescriptor pd = new PropertyDescriptor(fieldName, o.getClass());
			if (pd.getReadMethod() == null) {
				// get field anyway?
				// Field f = o.getClass().getDeclaredField(propName);
				// f.setAccessible(true);
				// f.get(o);
				return null;
			}
			return pd.getReadMethod().invoke(o);
		} catch (IntrospectionException e) {
			try{
				return getValueDirectFromField(o, fieldName);
			} catch(Exception ex) {
				throw new NoSuchPropertyException("Invalid poperty in select or expand path: " + fieldName);
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new PropertyError("Unable to get property in object: " + fieldName);
		}
	}

	public static Field getFieldForType(Object o, String name) throws NoSuchFieldException {
		Class<?> clazz=o.getClass();
		return getFieldForType(o.getClass(), name);
	}
	public static Field getFieldForType(Class<?> clazz, String name) throws NoSuchFieldException {
			while(clazz!=null) {
				try {
					return  clazz.getDeclaredField(name);
				} catch (NoSuchFieldException e) {
					//move up the type chain
					clazz=clazz.getSuperclass();
				}
			}
			throw new NoSuchFieldException("No field in type chain: "+name);
	}
	@SuppressWarnings("unchecked")
	public static <T> T  getEnumFromValue(Class<Enum> clazz,String name) {
		return (T) Enum.valueOf(clazz, name);
	}

	public static <T> T getEnumFromValue(Class<Enum> clazz,Object name) {
		if(name==null) {
			return null;
		}
		return getEnumFromValue(clazz, name.toString());
	}

	public static Enum<?> getEnumFromValue(Class<Enum> clazz,Integer index) {
		return clazz.getEnumConstants()[index];
	}


}
