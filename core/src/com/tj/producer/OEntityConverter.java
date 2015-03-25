package com.tj.producer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.odata4j.producer.QueryInfo;

import com.tj.datastructures.PropertyPath;
import com.tj.exceptions.IllegalRequestException;
import com.tj.producer.annotations.entity.IgnoreRead;
import com.tj.producer.util.ReflectionUtil;

public class OEntityConverter {

	public static Map<String, OLink> getLinks(EdmEntityType type, Object o, PropertyPath expandList,
			PropertyPath select, EdmDataServices service) {
		Map<String, OLink> props = new HashMap<String, OLink>();
		for (EdmNavigationProperty prop : type.getNavigationProperties()) {
			boolean doSelect = (select.getPathComponent() == null && select.isLeaf())
					|| select.nextComponentContains(prop.getName());
			if (!doSelect) {
				continue;
			}
			String relName = prop.getRelationship().getName();
			String propName = prop.getName();

			try {
				// only read the value if read is not blocked
				if (ReflectionUtil.getFieldForType(o,propName).isAnnotationPresent(IgnoreRead.class)) {
					continue;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException("Unable to get property in object: " + propName);
			}
			boolean doExpand = !expandList.isLeaf() && expandList.nextComponentContains(propName);
			EdmEntitySet target = service.getEdmEntitySet(prop.getToRole().getType().getName());
			if (doExpand) {
				Object value = ReflectionUtil.getFieldValue(o, propName);
				if (value == null) {

					props.put(prop.getName(),OLinks.relatedEntityInline(relName, propName, "url", null));
					continue;
				}
				if (Iterable.class.isAssignableFrom(value.getClass())) {
					List<OEntity> entities = new ArrayList<OEntity>();
					for (Object obj : (Iterable<?>) value) {
						OEntity entity = createOEntity(service, obj, target, expandList.getSubPath(propName),
								select.getSubPath(propName));
						entities.add(entity);
					}
					props.put(propName, OLinks.relatedEntitiesInline(relName, propName, "url", entities));
				} else {
					OEntity entity = createOEntity(service, value, target, expandList.getSubPath(propName),
							select.getSubPath(propName));

					props.put(propName, OLinks.relatedEntityInline(relName, propName, "url", entity));
				}
			} else {
				props.put(propName, OLinks.relatedEntity(relName, propName, "url"));
			}
		}
		return props;
	}

	public static List<OProperty<?>> getPropertiesList(EdmDataServices service, List<String> keys,
			EdmStructuralType type, Object o, PropertyPath select) {
		return new ArrayList<OProperty<?>>(getProperties(service, keys, type, o, select).values());
	}

	public static Map<String, OProperty<?>> getProperties(EdmDataServices service, List<String> keys,
			EdmStructuralType type, Object o, PropertyPath select) {
		Map<String, OProperty<?>> props = new HashMap<String, OProperty<?>>();
		for (EdmProperty prop : type.getProperties()) {
			try {
				// only read the value if read is not blocked
				if (ReflectionUtil.getFieldForType(o,prop.getName()).isAnnotationPresent(IgnoreRead.class)) {
					continue;
				}
			} catch (NoSuchFieldException | SecurityException e) {
				throw new RuntimeException("Unable to get property in object: " + prop.getName());
			}
			boolean doSelect = (keys != null && keys.contains(prop.getName())) || select.isLeaf()
					|| select.nextComponentContains(prop.getName());
			if (!doSelect) {
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
						getCollection(service, prop, value));
			} else if (value != null) {
				EdmComplexType ctype = (EdmComplexType) prop.getType();
				Map<String, OProperty<?>> properties = getProperties(service, null, ctype, value, select);
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

	public static OCollection<? extends OObject> getCollection(EdmDataServices service, EdmProperty prop, Object value) {
		if(!value.getClass().isArray()) {
			if (!Iterable.class.isAssignableFrom(value.getClass())) {
				throw new RuntimeException("Not a collection: " + prop.getName());
			}
			if (!EdmCollectionType.class.isAssignableFrom(prop.getType().getClass())) {
				throw new RuntimeException("Property Type must be a collection tpye: " + prop.getName());
			}
		}
		return getCollection(service, (EdmCollectionType) prop.getType(), value);
	}

	public static OCollection<? extends OObject> getCollection(EdmDataServices service, EdmCollectionType type,
			Object value) {
		OCollection.Builder<OObject> builder = OCollections.newBuilder(type.getItemType());
		if(value.getClass().isArray()) {
			value=Arrays.asList((Object[])value);
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
						PropertyPath.getEmptyPropertyPath());
				builder.add(OComplexObjects.create((EdmComplexType) itemType, props));
			} else if (itemType instanceof EdmEntityType) {
				builder.add(createOEntityCheckType(service, item, (EdmEntityType) itemType));
			}
		}
		return builder.build();
	}

	public static OComplexObject createComplexObject(EdmDataServices build, Object o, EdmComplexType type,
			QueryInfo query) {
		PropertyPath path;
		if (query == null) {
			path = PropertyPath.getEmptyPropertyPath();
		} else {
			path = new PropertyPath(query.select);
		}
		List<OProperty<?>> propertyList = getPropertiesList(build, null, type, o, path);
		return OComplexObjects.create(type, propertyList);
	}

	public static OEntity createOEntity(EdmDataServices build, Object o,Class<?> type) {
		PropertyPath expand = PropertyPath.getEmptyPropertyPath();
		PropertyPath select = PropertyPath.getEmptyPropertyPath();
		return createOEntity(build, o,type, expand, select);
	}
	public static OEntity createOEntity(EdmDataServices build, Object o) {
		return createOEntity(build, o,o.getClass());
	}
	public static OEntity createOEntity(EdmDataServices service, Object o, QueryInfo info) {
		return createOEntity(service, o, o.getClass(),info);
	}
	public static OEntity createOEntity(EdmDataServices service, Object o,Class<?> type, QueryInfo info) {
		PropertyPath expand = new PropertyPath(info.expand);
		PropertyPath select = new PropertyPath(info.select);
		return createOEntity(service, o, type, expand, select);
	}
	public static OEntity createOEntity(EdmDataServices service, Object o,EdmEntitySet type, QueryInfo info) {
		PropertyPath expand = new PropertyPath(info.expand);
		PropertyPath select = new PropertyPath(info.select);
		return createOEntity(service, o, type, expand, select);
	}
	public static OEntity createOEntityCheckType(EdmDataServices service, Object o, EdmEntityType check) {
		return createOEntityCheckType(service, o,o.getClass(),check);
	}
	public static OEntity createOEntityCheckType(EdmDataServices service, Object o,Class<?> type, EdmEntityType check) {
		EdmEntitySet set = service.getEdmEntitySet(check);
		if (!check.equals(set.getType())) {
			throw new IllegalArgumentException("Unexpected type found: ");
		}
		return createOEntity(service, o);
	}

	public static OEntity createOEntity(EdmDataServices service, Object o,Class<?> type, PropertyPath expand, PropertyPath select) {
		EdmEntitySet set = service.getEdmEntitySet(type.getSimpleName());
		return createOEntity(service, o, set, expand, select);
	}

	public static OEntity createOEntity(EdmDataServices service, Object o, EdmEntitySet set, PropertyPath expand,
			PropertyPath select) {
		Map<String, OProperty<?>> properties = getProperties(service, set.getType().getKeys(), set.getType(), o, select);
		List<String> keys = set.getType().getKeys();
		OEntityKey entityKey = OEntityKey.create(getKeyMap(keys, properties));
		Map<String, OLink> linkMap = getLinks(set.getType(), o, expand, select, service);
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
	public static Object oEntityToObject(OStructuralObject object, Class<?> type) {
		return oEntityToObject(object, type,new HashMap<String,Object>());
	}
	public static Object oEntityToObject(OStructuralObject object, Class<?> type,Map<String,Object> relations) {
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
				if(relations.containsKey(pd.getName())) {
					invokeSetter(ret, pd, relations.get(pd.getName()));
					continue;
				}
				OProperty<?> propertyInfo = object.getProperty(pd.getName());
				if (propertyInfo != null) {
					if (pd.getPropertyType().isEnum()) {

						Class<? extends Enum<?>> e = (Class<? extends Enum<?>>) pd.getPropertyType();
						Enum<?>[] constants = e.getEnumConstants();
						for (Enum<?> c : constants) {
							if (c.name().equalsIgnoreCase(propertyInfo.getValue().toString())) {
								invokeSetter(ret, pd, c);
								break;
							}
						}

					} else if (propertyInfo.getType().isSimple()) {
						invokeSetter(ret, pd, propertyInfo.getValue());
					} else if (EdmType.getInstanceType(propertyInfo.getType()) == OEntity.class) {
						Class<?> propType = Class.forName(propertyInfo.getType().getFullyQualifiedTypeName());
						Object o = oEntityToObject((OEntity) propertyInfo.getValue(), propType);
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
		Pattern p=Pattern.compile("\\([^()]+\\)$");
		Matcher m=p.matcher(href);
		boolean hasMatch=m.find();
		if(!hasMatch || m.end()!=href.length()) {
			throw new IllegalRequestException("Not a legal url");
		}
		return href.substring(m.start(), m.end());
	}
	public static OEntityKey getKeyFromHref(String href) {

		return OEntityKey.parse(getKeyFromUrl(href));
	}

}
