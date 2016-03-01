package com.tj.producer.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.odata4j.exceptions.BadRequestException;
import org.reflections.Reflections;

import com.tj.exceptions.NoSuchPropertyException;
import com.tj.exceptions.PropertyError;
import com.tj.producer.annotations.entity.IgnoreType;

public class ReflectionUtil {
	public static Collection<Class<?>> getMarkedClassesInPackage(String packageName, Class<? extends Annotation> marker) {
		Reflections reflect = createReflectionsObject(packageName);
		return reflect.getTypesAnnotatedWith(marker);
	}

	private static Reflections createReflectionsObject(String packageName) {
		return new Reflections(packageName);
	}

	public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
		return clazz == Boolean.class ||
				clazz == String.class ||
				clazz == Character.class ||
				clazz == Byte.class ||
				clazz == Short.class ||
				clazz == Integer.class ||
				clazz == Long.class ||
				clazz == Float.class ||
				clazz == Double.class ||
				clazz == Void.class ||
				clazz == BigDecimal.class ||
				clazz == BigInteger.class ||
				clazz.isPrimitive();
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

	public static <T> Set<Class<? extends T>> getSubTypesInPackages(Collection<String> packages, Class<T> superType,
			Class<?>... ignore) {
		return getSubTypesInPackages(packages, superType, Arrays.asList(ignore));
	}

	public static <T> Set<Class<? extends T>> getSubTypesInPackages(Collection<String> packages, Class<T> superType,
			Collection<Class<?>> ignore) {
		Set<Class<?>> ignoreSet = new HashSet<>();
		if (ignore != null) {
			ignoreSet.addAll(ignore);
		}
		Set<Class<? extends T>> ret = new HashSet<Class<? extends T>>();
		for (String packageName : packages) {
			Reflections reflect = createReflectionsObject(packageName);
			for (Class<? extends T> candidate : reflect.getSubTypesOf(superType)) {
				if (!ignoreSet.contains(candidate) && !candidate.isAnnotationPresent(IgnoreType.class)) {
					ret.add(candidate);
				}
			}
		}
		return ret;
	}

	public static Object invokeGetter(Object obj, String propName) {
		try {
			return invokeGetter(obj, new PropertyDescriptor(propName, obj.getClass()));
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object invokeGetter(Object obj, PropertyDescriptor pd) {
		if (pd.getReadMethod() != null) {
			try {
				return pd.getReadMethod().invoke(obj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("No getter for property: " + pd.getName() + " in type "
				+ obj.getClass().getCanonicalName());
	}

	public static void invokeSetterOrAddToCollection(Object obj, String propName, Object setterValue) {
		try {
			PropertyDescriptor pd = new PropertyDescriptor(propName, obj.getClass());
			if (Collection.class.isAssignableFrom(pd.getPropertyType())) {
				((Collection) invokeGetter(obj, pd)).add(setterValue);
			} else {
				invokeSetter(obj, pd, setterValue);
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

	}

	public static void invokeSetter(Object obj, String propName, Object setterValue) {
		try {
			invokeSetter(obj, new PropertyDescriptor(propName, obj.getClass()), setterValue);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setField(Object obj, String propName, Object setterValue) {
		try {
			Field f = obj.getClass().getDeclaredField(propName);
			setField(obj, f, setterValue);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setField(Object obj, Field f, Object setterValue) {
		try {
			f.setAccessible(true);
			f.set(obj, setterValue);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object getField(Object obj, String propName) {
		try {
			Field f = obj.getClass().getDeclaredField(propName);
			return getField(obj, f);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static Object getField(Object obj, Field f) {
		try {
			f.setAccessible(true);
			return f.get(obj);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
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

	public static Object getValueDirectFromField(Object o, String fieldName) throws IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException, SecurityException {
		// work up the heirarchy chain
		Class<?> clazz = o.getClass();
		while (clazz != null) {
			try {
				Field f = clazz.getDeclaredField(fieldName);
				f.setAccessible(true);
				return f.get(o);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException("Field " + fieldName + " does not exist in " + o.getClass().getCanonicalName()
				+ " or its supertypes.");
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
			try {
				return getValueDirectFromField(o, fieldName);
			} catch (Exception ex) {
				throw new NoSuchPropertyException("Invalid poperty in select or expand path: " + fieldName);
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new PropertyError("Unable to get property in object: " + fieldName, e);
		}
	}

	public static Field getFieldForType(Object o, String name) throws NoSuchFieldException {
		return getFieldForType(o.getClass(), name);
	}

	public static Field getFieldForType(Class<?> clazz, String name) throws NoSuchFieldException {
		while (clazz != null) {
			try {
				return clazz.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				// move up the type chain
				clazz = clazz.getSuperclass();
			}
		}
		throw new NoSuchFieldException("No field in type chain: " + name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getEnumFromValue(Class<Enum> clazz, String name) {
		return (T) Enum.valueOf(clazz, name);
	}

	public static <T> T getEnumFromValue(Class<Enum> clazz, Object name) {
		if (name == null) {
			return null;
		}
		return getEnumFromValue(clazz, name.toString());
	}

	public static Enum<?> getEnumFromValue(Class<Enum> clazz, Integer index) {
		return clazz.getEnumConstants()[index];
	}

	public static Class<?> getCollectionType(Class<?> startingClass) {
		if (!Collection.class.isAssignableFrom(startingClass)) {
			return null;
		}
		while (startingClass != null) {
			Type[] interfaces = startingClass.getGenericInterfaces();
			for (Type t : interfaces) {
				if (t instanceof ParameterizedType && ((ParameterizedType) t).getRawType() == Collection.class) {
					return (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
				}
			}
			startingClass = startingClass.getSuperclass();
		}
		return null;
	}

	public static <T> T newDefaultInstance(Class<T> idType) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<T> constructor = idType.getDeclaredConstructor();
		constructor.setAccessible(true);
		return constructor.newInstance();
	}

	public static boolean canInstantiate(Class<?> type) {
		return type != null && !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
	}

	public static Collection<Object> getCollectionObjectForProperty(PropertyDescriptor pd, Collection<?> value)
			throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Collection<Object> tmpObject;
		if (ReflectionUtil.canInstantiate(pd.getPropertyType())) {
			tmpObject = (Collection<Object>) ReflectionUtil.newDefaultInstance(pd.getPropertyType());
			tmpObject.addAll(value);
		} else {
			if (pd.getPropertyType() == List.class || pd.getPropertyType() == Collection.class) {
				tmpObject = new ArrayList<Object>(value);
			} else if (pd.getPropertyType() == Queue.class) {
				tmpObject = new LinkedList<Object>(value);
			} else if (pd.getPropertyType() == Set.class) {
				tmpObject = new HashSet<Object>(value);
			} else {
				throw new BadRequestException(pd.getPropertyType().getCanonicalName()
						+ " is not a supported collection interface.");
			}
		}
		return tmpObject;
	}

	public static List<Field> getFieldsWithAnyAnnotation(Class<?> type, Class<? extends Annotation>... annotations) {
		List<Field> ret = new ArrayList<Field>();
		while (type != null) {
			for (Field f : type.getDeclaredFields()) {
				for (Class<? extends Annotation> a : annotations) {
					if (f.isAnnotationPresent(a)) {
						ret.add(f);
					}
				}
			}
			type = type.getSuperclass();
		}
		return ret;
	}
}
