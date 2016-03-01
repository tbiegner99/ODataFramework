package com.tj.producer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.odata4j.core.OCollection;
import org.odata4j.core.OCollections;
import org.odata4j.core.OComplexObject;
import org.odata4j.core.OComplexObjects;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.core.OObject;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.core.OSimpleObject;
import org.odata4j.core.OSimpleObjects;
import org.odata4j.core.OStructuralObject;
import org.odata4j.edm.EdmCollectionType;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmStructuralType;
import org.odata4j.edm.EdmType;
import org.odata4j.exceptions.BadRequestException;
import org.odata4j.producer.QueryInfo;

import com.tj.datastructures.PropertyPath;
import com.tj.exceptions.IllegalRequestException;
import com.tj.odata.extensions.EdmJavaTypeConverter;
import com.tj.producer.annotations.entity.IgnoreRead;
import com.tj.producer.annotations.entity.UpdateDate;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.producer.util.ReflectionUtil;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.user.User;

public class OEntityConverter {
	public static Map<String, OLink> getLinks(EdmEntityType type, Object o, PropertyPath expandList,
			PropertyPath select, EdmDataServices service, ProducerConfiguration cfg, User user) {
		CompositeSecurityManager securityManager = (cfg == null ? null : cfg.getSecurityManager());
		Map<String, OLink> props = new HashMap<String, OLink>();
		for (EdmNavigationProperty prop : type.getNavigationProperties()) {
			boolean doSelect = (select.getPathComponent() == null && select.isLeaf())
					|| select.nextComponentContains(prop.getName());

			String relName = prop.getRelationship().getName();
			String propName = prop.getName();
			//
			if (!doSelect
					|| (securityManager != null && (!securityManager.canAccessProperty(o, propName, user, cfg) || !securityManager
							.canReadProperty(o, propName, user, cfg)))) {
				continue;
			}
			try {
				// only read the value if read is not blocked
				if (ReflectionUtil.getFieldForType(o, propName).isAnnotationPresent(IgnoreRead.class)) {
					continue;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException("Unable to get property in object: " + propName);
			}
			boolean doExpand = !expandList.isLeaf() && expandList.nextComponentContains(propName);
			EdmEntitySet target = service.getEdmEntitySet(prop.getToRole().getType().getName());
			if (doExpand) {
				Object value = ReflectionUtil.invokeGetter(o, propName);// ReflectionUtil.getFieldValue(o, propName);
				if (value == null) {
					props.put(prop.getName(), OLinks.relatedEntityInline(relName, propName, "url", null));
					continue;
				}
				if (Iterable.class.isAssignableFrom(value.getClass())) {
					List<OEntity> entities = new ArrayList<OEntity>();
					for (Object obj : (Iterable<?>) value) {
						OEntity entity = createOEntity(service, obj, target, expandList.getSubPath(propName),
								select.getSubPath(propName), cfg, user);
						entities.add(entity);
					}
					props.put(propName, OLinks.relatedEntitiesInline(relName, propName, "url", entities));
				} else {
					OEntity entity = createOEntity(service, value, target, expandList.getSubPath(propName),
							select.getSubPath(propName), cfg, user);

					props.put(propName, OLinks.relatedEntityInline(relName, propName, "url", entity));
				}
			} else {
				props.put(propName, OLinks.relatedEntity(relName, propName, "url"));
			}
		}
		return props;
	}

	public static List<OProperty<?>> getPropertiesList(EdmDataServices service, List<String> keys,
			EdmStructuralType type, Object o, PropertyPath select, ProducerConfiguration cfg, User user) {
		return new ArrayList<OProperty<?>>(getProperties(service, keys, type, o, select, cfg, user).values());
	}

	public static Map<String, OProperty<?>> getProperties(EdmDataServices service, List<String> keys,
			EdmStructuralType type, Object o, PropertyPath select, ProducerConfiguration cfg, User user) {
		CompositeSecurityManager securityManager = (cfg == null ? null : cfg.getSecurityManager());
		if (type instanceof EdmComplexType && securityManager != null
				&& securityManager.getSecurityManagerForClass(o.getClass()) == null) {
			securityManager = null;
		}
		Map<String, OProperty<?>> props = new HashMap<String, OProperty<?>>();
		for (EdmProperty prop : type.getProperties()) {
			try {
				// only read the value if read is not blocked
				if (ReflectionUtil.getFieldForType(o, prop.getName()).isAnnotationPresent(IgnoreRead.class)) {
					continue;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException("Unable to get property in object: " + prop.getName());
			}
			boolean doSelect = (keys != null && keys.contains(prop.getName())) || select.isLeaf()
					|| select.nextComponentContains(prop.getName());
			boolean canAccessProp = keys.contains(prop.getName())
					|| securityManager == null
					|| (securityManager.canAccessProperty(o, prop.getName(), user, cfg) && securityManager
							.canReadProperty(o, prop.getName(), user, cfg));
			if (!doSelect || !canAccessProp) {
				continue;
			}
			OProperty<?> property = null;
			Object value = ReflectionUtil.getFieldValue(o, prop.getName());
			if (prop.getType().isSimple()) {
				if (value == null) {
					property = OProperties.null_(prop.getName(), (EdmSimpleType<?>) prop.getType());
				} else if (value.getClass().isEnum()) {
					property = OProperties.simple(prop.getName(), (EdmSimpleType<?>) prop.getType(), value.toString());
				} else {
					property = OProperties.simple(prop.getName(), (EdmSimpleType<?>) prop.getType(), value);
				}
			} else if (prop.getType() instanceof EdmCollectionType) {
				property = OProperties.collection(prop.getName(), (EdmCollectionType) prop.getType(),
						getCollection(service, prop, value, cfg, user, null));
			} else if (value != null) {
				EdmComplexType ctype = (EdmComplexType) prop.getType();
				Map<String, OProperty<?>> properties = getProperties(service, null, ctype, value, select, cfg, user);
				property = OProperties.complex(prop.getName(), ctype, new ArrayList<OProperty<?>>(properties.values()));
			}
			if (property != null) {
				props.put(prop.getName(), property);
			}
		}
		return props;
	}

	private static void getComplexAttributes(Map<String, Object> map, OProperty<?> property) {
		for (OProperty<?> prop : ((Iterable<OProperty<?>>) property.getValue())) {
			if (prop.getType().isSimple()) {
				map.put(prop.getName(), prop.getValue());
			} else {
				getComplexAttributes(map, prop);
			}
		}
	}

	private static Map<String, Object> getKeyMap(List<String> keys, Map<String, OProperty<?>> properties) {
		Map<String, Object> keyMap = new HashMap<String, Object>();
		for (String k : keys) {
			OProperty<?> prop = properties.get(k);
			if (prop == null || prop.getValue() == null) {
				continue;
			}
			if (prop.getType().isSimple()) {
				keyMap.put(k, prop.getValue());
			} else {
				getComplexAttributes(keyMap, prop);
			}
		}
		return keyMap;
	}

	public static OCollection<? extends OObject> getCollection(EdmDataServices service, EdmProperty prop, Object value,
			ProducerConfiguration cfg, User user, QueryInfo info) {
		if (!value.getClass().isArray()) {
			if (!Iterable.class.isAssignableFrom(value.getClass())) {
				throw new RuntimeException("Not a collection: " + prop.getName());
			}
			if (!EdmCollectionType.class.isAssignableFrom(prop.getType().getClass())) {
				throw new RuntimeException("Property Type must be a collection tpye: " + prop.getName());
			}
		}
		return getCollection(service, (EdmCollectionType) prop.getType(), value, cfg, user, info);
	}

	public static OCollection<? extends OObject> getCollection(EdmDataServices service, EdmCollectionType type,
			Object value, ProducerConfiguration cfg, User user, QueryInfo info) {
		OCollection.Builder<OObject> builder = OCollections.newBuilder(type.getItemType());
		if (value == null) {
			value = new Object[0];
		}
		if (value.getClass().isArray()) {
			value = Arrays.asList((Object[]) value);
		}
		if (!Iterable.class.isAssignableFrom(value.getClass())) {
			throw new RuntimeException("Object is not a collection");
		}
		if (!EdmCollectionType.class.isAssignableFrom(type.getClass())) {
			throw new RuntimeException("Type must be a collection tpye");
		}
		EdmType itemType = type.getItemType();
		for (Object item : (Iterable<?>) value) {
			if (itemType.isSimple()) {
				builder.add(OSimpleObjects.create((EdmSimpleType<?>) itemType, item));
			} else if (itemType instanceof EdmComplexType) {
				List<OProperty<?>> props = getPropertiesList(service, null, (EdmComplexType) itemType, item,
						PropertyPath.getEmptyPropertyPath(), cfg, user);
				builder.add(OComplexObjects.create((EdmComplexType) itemType, props));
			} else if (itemType instanceof EdmEntityType) {
				builder.add(createOEntityCheckType(service, item, (EdmEntityType) itemType, cfg, user, info));
			}
		}
		return builder.build();
	}

	public static OComplexObject createComplexObject(EdmDataServices build, Object o, EdmComplexType type,
			QueryInfo query, ProducerConfiguration cfg, User user) {
		PropertyPath path;
		if (query == null) {
			path = PropertyPath.getEmptyPropertyPath();
		} else {
			path = new PropertyPath(query.select);
		}
		List<OProperty<?>> propertyList = getPropertiesList(build, null, type, o, path, cfg, user);
		return OComplexObjects.create(type, propertyList);
	}

	public static OEntity createOEntity(EdmDataServices build, Object o, Class<?> type, ProducerConfiguration cfg,
			User user) {
		PropertyPath expand = PropertyPath.getEmptyPropertyPath();
		PropertyPath select = PropertyPath.getEmptyPropertyPath();
		return createOEntity(build, o, type, expand, select, cfg, user);
	}

	public static OEntity createOEntity(EdmDataServices build, Object o, ProducerConfiguration cfg, User user) {
		return createOEntity(build, o, o.getClass(), cfg, user);
	}

	public static OEntity createOEntity(EdmDataServices service, Object o, QueryInfo info, ProducerConfiguration cfg,
			User user) {
		return createOEntity(service, o, o.getClass(), info, cfg, user);
	}

	public static OEntity createOEntity(EdmDataServices service, Object o, Class<?> type, QueryInfo info,
			ProducerConfiguration cfg, User user) {
		PropertyPath expand = new PropertyPath(info.expand);
		PropertyPath select = new PropertyPath(info.select);
		return createOEntity(service, o, type, expand, select, cfg, user);
	}

	public static OEntity createOEntity(EdmDataServices service, Object o, EdmEntitySet type, QueryInfo info,
			ProducerConfiguration cfg, User user) {
		PropertyPath expand = new PropertyPath(info.expand);
		PropertyPath select = new PropertyPath(info.select);
		return createOEntity(service, o, type, expand, select, cfg, user);
	}

	public static OEntity createOEntityCheckType(EdmDataServices service, Object o, EdmEntityType check,
			ProducerConfiguration cfg, User user, QueryInfo info) {
		return createOEntityCheckType(service, o, o.getClass(), check, cfg, user, info);
	}

	public static OEntity createOEntityCheckType(EdmDataServices service, Object o, Class<?> type, EdmEntityType check,
			ProducerConfiguration cfg, User user, QueryInfo info) {
		EdmEntitySet set = service.getEdmEntitySet(check);
		if (!check.equals(set.getType())) {
			throw new IllegalArgumentException("Unexpected type found: ");
		}
		if (info != null) {
			return createOEntity(service, o, info, cfg, user);
		} else {
			return createOEntity(service, o, cfg, user);
		}
	}

	public static OEntity createOEntity(EdmDataServices service, Object o, Class<?> type, PropertyPath expand,
			PropertyPath select, ProducerConfiguration cfg, User user) {
		EdmEntitySet set = service.getEdmEntitySet(type.getSimpleName());
		return createOEntity(service, o, set, expand, select, cfg, user);
	}

	public static OEntity createOEntity(EdmDataServices service, Object o, EdmEntitySet set, PropertyPath expand,
			PropertyPath select, ProducerConfiguration cfg, User user) {

		Map<String, OProperty<?>> properties = getProperties(service, set.getType().getKeys(), set.getType(), o,
				select, cfg, user);
		List<String> keys = set.getType().getKeys();
		OEntityKey entityKey = OEntityKey.create(getKeyMap(keys, properties));
		Map<String, OLink> linkMap = getLinks(set.getType(), o, expand, select, service, cfg, user);
		List<OLink> links = new ArrayList<OLink>(linkMap.values());
		ArrayList<OProperty<?>> propertyList = new ArrayList<OProperty<?>>(properties.values());
		OEntity ret = OEntities.create(set, set.getType(), entityKey, propertyList, links);
		return ret;
	}

	public static Object tryConvertToDate(Class<?> target, Object object) {
		if (target == java.util.Date.class) {
			if (object.getClass() == LocalDateTime.class) {
				return ((LocalDateTime) object).toDateTime().toDate();
			} else if (object.getClass() == DateTime.class) {
				return ((DateTime) object).toDate();
			} else if (object.getClass() == LocalTime.class) {
				return ((LocalTime) object).toDateTimeToday().toDate();
			}
		} else if (target == java.sql.Date.class) {
			if (object.getClass() == LocalDateTime.class) {
				return new java.sql.Date(((LocalDateTime) object).toDateTime().getMillis());
			} else if (object.getClass() == DateTime.class) {
				return new java.sql.Date(((DateTime) object).getMillis());
			} else if (object.getClass() == LocalTime.class) {
				return new java.sql.Date(((LocalTime) object).toDateTimeToday().getMillis());
			}
		} else if (target == java.util.Calendar.class) {
			if (object.getClass() == LocalDateTime.class) {
				return ((LocalDateTime) object).toDateTime().toGregorianCalendar();
			} else if (object.getClass() == DateTime.class) {
				return ((DateTime) object).toGregorianCalendar();
			} else if (object.getClass() == LocalTime.class) {
				return ((LocalTime) object).toDateTimeToday().toGregorianCalendar();
			}
		} else if (target == org.joda.time.LocalTime.class) {
			if (object.getClass() == LocalDateTime.class) {
				return ((LocalDateTime) object).toLocalTime();
			} else if (object.getClass() == DateTime.class) {
				return ((DateTime) object).toLocalTime();
			}
		} else if (target == org.joda.time.LocalDateTime.class) {
			if (object.getClass() == LocalTime.class) {
				return ((LocalTime) object).toDateTimeToday().toLocalDateTime();
			} else if (object.getClass() == DateTime.class) {
				return ((DateTime) object).toLocalDateTime();
			}
		} else if (target == org.joda.time.DateTime.class) {
			if (object.getClass() == LocalDateTime.class) {
				return ((LocalDateTime) object).toLocalTime();
			} else if (object.getClass() == LocalTime.class) {
				return ((LocalTime) object).toDateTimeToday();
			}
		}
		return object;
	}

	public static Object getNow(Class<?> target) {
		if (target == java.util.Date.class) {
			return new java.util.Date();
		} else if (target == java.sql.Date.class) {
			return new java.sql.Date(new java.util.Date().getTime());
		} else if (target == java.util.Calendar.class) {
			return new GregorianCalendar();
		} else if (target == org.joda.time.LocalTime.class) {
			return new LocalTime();
		} else if (target == org.joda.time.LocalDateTime.class) {
			return new LocalDateTime();
		} else if (target == org.joda.time.DateTime.class) {
			return new DateTime();
		}
		throw new IllegalArgumentException(target.getName() + " is not a known date type.");
	}

	public static Object getComplexType(List<OProperty<?>> props, Class<?> type) {
		Object ret;
		try {
			Constructor<?> c = type.getDeclaredConstructor();
			c.setAccessible(true);
			ret = c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException();
		}
		// BeanInfo info= Introspector.getBeanInfo(type);
		for (OProperty<?> prop : props) {
			try {
				Field f = type.getDeclaredField(prop.getName());
				f.setAccessible(true);
				f.set(ret, prop.getValue());
			} catch (Exception e) {
				throw new RuntimeException();
			}
		}
		return ret;
	}

	public static Object oEntityToObject(OStructuralObject object, Class<?> type, boolean validate) {
		return oEntityToObject(object, type, new HashMap<String, Object>(), validate);
	}

	public static void setUpdateDate(Object entityToUpdate) {
		setDynamicDate(entityToUpdate, UpdateDate.class);

	}

	public static void setDynamicDate(Object entity, Class<? extends Annotation>... annotations) {
		if (entity == null) {
			return;
		}
		Class<?> type = entity.getClass();
		while (type != null) {
			for (Field f : type.getDeclaredFields()) {
				for (Class<? extends Annotation> annotation : annotations) {
					if (f.isAnnotationPresent(annotation)) {
						try {
							f.setAccessible(true);
							f.set(entity, getNow(f.getType()));
							break;
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new IllegalArgumentException("Dynamic date could not be set for field: "
									+ f.getName(), e);
						}
					}
				}
			}
			type = type.getSuperclass();
		}

	}

	public static Object oEntityToObject(OStructuralObject object, Class<?> type, Map<String, Object> relations,
			boolean validate) {
		return oEntityToObject(object, type, relations, null, validate);
	}

	private static boolean findAutoDates(Class<?> type, Object ret, PropertyDescriptor pd,
			List<Class<? extends Annotation>> annotations) {
		try {
			if (annotations != null) {
				Field f = null;
				while (type != null && f == null) {
					try {
						f = type.getDeclaredField(pd.getName());
					} catch (NoSuchFieldException | SecurityException e) {
						type = type.getSuperclass();
						continue;
					}

					for (Class<? extends Annotation> annotation : annotations) {
						if (f.isAnnotationPresent(annotation)) {
							f.setAccessible(true);
							f.set(ret, getNow(f.getType()));
							return true;
						}
					}
				}

			}
		} catch (Exception e) {
		}
		return false;
	}

	public static Object oEntityToObject(OStructuralObject object, Class<?> type, Map<String, Object> relations,
			List<Class<? extends Annotation>> annotations, boolean validate) {
		BeanInfo info;
		Object ret;
		try {
			info = Introspector.getBeanInfo(type);
			ret = type.newInstance();
		} catch (IntrospectionException e) {
			throw new RuntimeException();
		} catch (InstantiationException e) {
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			throw new RuntimeException();
		}
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			try {
				if (findAutoDates(type, ret, pd, annotations)) {
					continue;
				}

				if (relations.containsKey(pd.getName())) {
					Object value = relations.get(pd.getName());
					try {
						if (value != null && Collection.class.isAssignableFrom(pd.getPropertyType())) {
							value = ReflectionUtil.getCollectionObjectForProperty(pd, (Collection<?>) value);
						}
					} catch (ClassCastException | NoSuchMethodException | InstantiationException
							| IllegalAccessException | InvocationTargetException e) {
						throw new BadRequestException("Problem converting collection for " + pd.getName()
								+ ". Collection must have default constructor or be an interface with recognized type.");
					}
					invokeSetter(ret, pd, value);
					continue;
				}
				OProperty<?> propertyInfo = object.getProperty(pd.getName());
				if (propertyInfo != null) {
					if (propertyInfo.getType() instanceof EdmCollectionType) {
						EdmType itemType = ((EdmCollectionType) propertyInfo.getType()).getItemType();
						if (itemType instanceof EdmSimpleType<?>) {
							try {
								Iterator<? extends OSimpleObject<Object>> it = ((OCollection<? extends OSimpleObject<Object>>) propertyInfo
										.getValue()).iterator();
								List<Object> collection = new ArrayList<Object>();
								while (it.hasNext()) {
									Object val = it.next().getValue();
									if (validate) {
										//validateField()
									}
									collection.add(val);
								}
								Object value = ReflectionUtil.getCollectionObjectForProperty(pd, collection);
								invokeSetter(ret, pd, value);
							} catch (ClassCastException | NoSuchMethodException | InstantiationException
									| IllegalAccessException | InvocationTargetException e) {
								throw new BadRequestException(
										"Problem converting collection for "
												+ pd.getName()
												+ ". Collection must have default constructor or be an interface with recognized type.");
							}
						} else {
							throw new IllegalArgumentException("Unexpected Item Type in collection for property ("
									+ propertyInfo.getName() + ") : " + itemType.getFullyQualifiedTypeName());
						}
					} else if (pd.getPropertyType().isEnum()) {
						Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) pd.getPropertyType();
						Enum<?>[] constants = e.getEnumConstants();
						for (Enum<?> c : constants) {
							if (c.name().equalsIgnoreCase(propertyInfo.getValue().toString())) {
								invokeSetter(ret, pd, c);
								break;
							}
						}

					} else if (propertyInfo.getType().isSimple()) {
						Object value = EdmJavaTypeConverter.convertToClass((EdmSimpleType<?>) propertyInfo.getType(),
								propertyInfo.getValue(), pd.getPropertyType());
						invokeSetter(ret, pd, value);
					} else if (EdmType.getInstanceType(propertyInfo.getType()) == OEntity.class) {
						Class<?> propType = Class.forName(propertyInfo.getType().getFullyQualifiedTypeName());
						Object o = oEntityToObject((OEntity) propertyInfo.getValue(), propType, validate);
						invokeSetter(ret, pd, o);
					} else {
						Class<?> propType = pd.getPropertyType();// keyTypes.get(EntityKey.SIMPLE_KEY);
						Object o = getComplexType((List<OProperty<?>>) propertyInfo.getValue(), propType);
						invokeSetter(ret, pd, o);
					}
				}
			} catch (RuntimeException | ClassNotFoundException e) {
				continue;
			}
		}
		return ret;
	}

	private static void invokeSetter(Object obj, PropertyDescriptor pd, Object setterValue) {
		setterValue = tryConvertToDate(pd.getPropertyType(), setterValue);
		ReflectionUtil.invokeSetter(obj, pd, setterValue);
	}

	public static String getKeyFromUrl(String href) {
		Pattern p = Pattern.compile("\\([^()]+\\)$");
		Matcher m = p.matcher(href);
		boolean hasMatch = m.find();
		if (!hasMatch || m.end() != href.length()) {
			throw new IllegalRequestException("Not a legal url");
		}
		return href.substring(m.start(), m.end());
	}

	public static OEntityKey getKeyFromHref(String href) {

		return OEntityKey.parse(getKeyFromUrl(href));
	}

}
