package com.tj.producer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import antlr.collections.List;

import com.tj.producer.annotations.entity.IgnoreUpdate;
import com.tj.producer.util.ReflectionUtil;


public class ObjectMerger {
	/***
	 * MERGES TWO OBJECTS. tAKES THE PROPERTY NAME OF THE SUBJET AND ATTEMPTS TO SET THE CORRESPONDING PROPERTY IN THE TARGET AS LONG AS THE VALUE IS THE SAME
	 * TYPE AND THE SUBJECT VALUE IS NOT NULL
	 *
	 * @param subject
	 * @param target
	 * @return
	 */
	public <S,T> T mergeObjects(S subject, T target) {
		return mergeObjects(subject, target, true);
	}

	public <S,T> T updateObjects(S subject, T target) {
		return mergeObjects(subject, target, false);
	}

	public <T> T setKeyProperties(KeyMap map, T target) {
		return map.setValuesToEntity(target);
	}

	@SuppressWarnings("rawtypes")
	private Class<? extends Collection> getCommonCollectionOfType(Class<?> clazz) {
		if (!Collection.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException("Class is not a collection");
		}
		if (Set.class.isAssignableFrom(clazz)) {
			return HashSet.class;
		} else if (Queue.class.isAssignableFrom(clazz)) {
			return LinkedList.class;
		} else if (List.class.isAssignableFrom(clazz) || clazz == Collection.class) {
			return ArrayList.class;
		}
		throw new IllegalArgumentException("Unknown Collection");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private  void tryToMergeCollections(Collection collection1, Object target, Field targetField) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		if (collection1 == null) {
			throw new IllegalArgumentException();
		}
		Collection collection2 = (Collection) ReflectionUtil.invokeGetter(target, targetField.getName());
		if (collection2 == null) {
			if (targetField.getType().isInterface() || Modifier.isAbstract(targetField.getType().getModifiers())) {
				collection2 = getCommonCollectionOfType(targetField.getType()).newInstance();
			} else {
				collection2 = (Collection) targetField.getType().newInstance();
			}
		}
		for (Object item : collection1) {
			collection2.add(item);
		}
		ReflectionUtil.invokeSetter(target,targetField.getName(),collection2);
	}

	private boolean isCompatableCollection(Field subjectField, Field targetField) {
		if (Collection.class.isAssignableFrom(targetField.getType()) && Collection.class.isAssignableFrom(subjectField.getType())) {
			ParameterizedType subject = (ParameterizedType) subjectField.getGenericType();
			ParameterizedType target = (ParameterizedType) targetField.getGenericType();
			return ((Class<?>) target.getActualTypeArguments()[0]).isAssignableFrom((Class<?>) subject.getActualTypeArguments()[0]);
		}
		return false;
	}

	private <S,T> T mergeObjects(S subject, T target, boolean doPatch) {
		Class<?> targetClass = target.getClass();
		Class<?> subjectClass = subject.getClass();
		while(targetClass!=Object.class) {
			for (Field targetField : targetClass.getDeclaredFields()) {
				try {
					Field subjectField = subjectClass.getDeclaredField(targetField.getName());
					if(targetField.isAnnotationPresent(IgnoreUpdate.class) || subjectField.isAnnotationPresent(IgnoreUpdate.class)) {
						continue;
					}
					if(Modifier.isStatic(targetField.getModifiers()) || Modifier.isStatic(subjectField.getModifiers())) {
						continue;
					}
					if (isCompatableCollection(subjectField, targetField)) {
						if (!doPatch && targetField.get(target) != null) {
							((Collection<?>) targetField.get(target)).clear();
						}
						tryToMergeCollections((Collection<?>) ReflectionUtil.invokeGetter(subject, targetField.getName()), target, targetField);
						continue;
					}
					if (!targetField.getType().isAssignableFrom(subjectField.getType())) {
						continue;
					}
					Object value = ReflectionUtil.getField(subject, subjectField);
					// primitives cant be null
					if (value == null && (doPatch || subjectField.getType().isPrimitive())) {
						continue;
					}

					ReflectionUtil.setField(target, targetField, value);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassCastException | InstantiationException e) {
					// maybe log here? Maybe a warning.
					continue;
				}
			}
			if(targetClass==subjectClass) { //for the same types, work up the heirarchy chain
				targetClass=targetClass.getSuperclass();
				subjectClass=subjectClass.getSuperclass();
			} else {
				break;
			}
		}
		return target;

	}

}
