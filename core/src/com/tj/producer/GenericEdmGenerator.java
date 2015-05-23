package com.tj.producer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;

import org.odata4j.core.PrefixedNamespace;
import org.odata4j.edm.EdmAssociation;
import org.odata4j.edm.EdmAssociationEnd;
import org.odata4j.edm.EdmCollectionType;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmDataServices.Builder;
import org.odata4j.edm.EdmDecorator;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmFunctionParameter;
import org.odata4j.edm.EdmGenerator;
import org.odata4j.edm.EdmMultiplicity;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmProperty.CollectionKind;
import org.odata4j.edm.EdmSchema;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmType;

import com.tj.odata.functions.FunctionInfo;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.producer.annotations.entity.IgnoreProperty;
import com.tj.producer.annotations.entity.Inherited;
import com.tj.producer.annotations.entity.Key;
import com.tj.producer.configuration.ProducerConfiguration;

public class GenericEdmGenerator implements EdmGenerator {

	private Map<String, Set<Class<?>>> classes;
	private Map<FunctionName, FunctionInfo> functions;
	private ProducerConfiguration config;

	public GenericEdmGenerator(ProducerConfiguration cfg) {
		classes = new HashMap<String, Set<Class<?>>>();
		addClasses(cfg.getEntityTypes());
		config = cfg;
		functions = new HashMap<FunctionName, FunctionInfo>();
		for (FunctionName name : cfg.getSupportedFunctions()) {
			functions.put(name, cfg.getFunctionInfo(name));
		}
	}

	private void addClasses(Collection<Class<?>> entityTypes) {
		for (Class<?> o : entityTypes) {
			if (!classes.containsKey(o.getPackage().getName())) {
				classes.put(o.getPackage().getName(), new HashSet<Class<?>>());
			}
			classes.get(o.getPackage().getName()).add(o);
		}
	}

	@Override
	public Builder generateEdm(EdmDecorator decorator) {
		return createEdm(decorator);
	}

	private Builder createEdm(EdmDecorator decorator) {
		List<EdmSchema.Builder> schemas = new ArrayList<EdmSchema.Builder>();
		List<PrefixedNamespace> namespaces = new ArrayList<PrefixedNamespace>();
		Map<Class<?>, EdmTypeInfo> types = new HashMap<Class<?>, EdmTypeInfo>();
		if (!functions.isEmpty()) {
			EdmSchema.Builder functionSchema = EdmSchema.newBuilder();
			Collection<EdmFunctionImport.Builder> functionImports = new ArrayList<EdmFunctionImport.Builder>();
			for (FunctionInfo function : functions.values()) {
				functionImports.add(getFunctionImport(function, types));
			}
			namespaces.add(new PrefixedNamespace("f", "functions"));
			functionSchema.setNamespace("functions").addEntityContainers(
					EdmEntityContainer.newBuilder().setName("functions").addFunctionImports(functionImports));
			schemas.add(functionSchema);
			addClasses(types.keySet());
		}
		for (String pack : classes.keySet()) {
			schemas.add(fromPackage(pack, types));
			namespaces.add(new PrefixedNamespace(pack, pack));
		}

		return EdmDataServices.newBuilder().addSchemas(schemas).addNamespaces(namespaces);
	}

	private EdmFunctionImport.Builder getFunctionImport(FunctionInfo function, Map<Class<?>, EdmTypeInfo> types) {
		EdmFunctionImport.Builder builder = EdmFunctionImport.newBuilder();
		builder.setHttpMethod(function.getName().getHttpMethod());
		builder.setName(function.getName().getName());
		List<EdmFunctionParameter.Builder> parameters = new ArrayList<EdmFunctionParameter.Builder>();
		for (Map.Entry<String, Class<?>> param : function.getParameters().entrySet()) {
			EdmFunctionParameter.Builder pbuild = EdmFunctionParameter.newBuilder();
			EdmTypeInfo info = getEdmType(param.getValue(), types);
			pbuild.setType(info.builder);
			pbuild.setName(param.getKey());
			pbuild.setNullable(!function.isParameterRequired(param.getKey()));
			parameters.add(pbuild);
		}
		builder.addParameters(parameters);
		if (function.getReturnType() != null) {
			EdmType.Builder<?, ?> ret = getEdmType(function.getReturnType(), types).builder;
			if (function.isCollectionReturned()) {
				ret = EdmCollectionType.newBuilder().setCollectionType(ret).setKind(CollectionKind.Collection);
			}
			builder.setReturnType(ret);
		}

		return builder;
	}

	private EdmSchema.Builder fromPackage(String name, Map<Class<?>, EdmTypeInfo> types) {
		List<EdmEntitySet.Builder> edmEntitySets = new ArrayList<EdmEntitySet.Builder>();
		List<EdmEntityType.Builder> edmEntityTypes = new ArrayList<EdmEntityType.Builder>();
		List<EdmComplexType.Builder> edmComplexTypes = new ArrayList<EdmComplexType.Builder>();
		List<EdmAssociation.Builder> associations = new ArrayList<EdmAssociation.Builder>();
		for (Class<?> c : classes.get(name)) {
			if (types.containsKey(c)) {
				continue;
			}
			types.put(c, getEdmType(c, types));
		}
		for (Class<?> clazz : types.keySet()) {
			EdmTypeInfo inf = types.get(clazz);
			if (!clazz.getPackage().getName().equals(name)) {
				continue;
			}
			switch (inf.type) {
				case COMPLEX:
					EdmComplexType.Builder ctbuilder = ((EdmComplexType.Builder) inf.builder);
					edmComplexTypes.add(ctbuilder);
					break;
				case ENTITY:
					String entityName = clazz.getSimpleName();
					if (inf.componentTypeInfo != null) {
						inf = inf.componentTypeInfo;
					}
					EdmEntityType.Builder builder = (EdmEntityType.Builder) inf.builder;
					edmEntitySets.add(EdmEntitySet.newBuilder().setEntityType(builder).setName(entityName));
					edmEntityTypes.add(builder);
					associations.addAll(inf.props.associations);
					break;
				default:
					continue;
			}
		}
		EdmEntityContainer.Builder cont = EdmEntityContainer.newBuilder().setName(name).addEntitySets(edmEntitySets);
		return EdmSchema.newBuilder().setNamespace(name).addEntityContainers(cont).addEntityTypes(edmEntityTypes)
				.addComplexTypes(edmComplexTypes).addAssociations(associations);
	}

	class PropertiesInfo {
		List<EdmNavigationProperty.Builder> navProperties;
		List<EdmAssociation.Builder> associations;
		List<EdmProperty.Builder> properties;
		List<String> keys;

		EdmEntityType.Builder constructBuilder(EdmEntityType.Builder builder) {
			return builder.addNavigationProperties(navProperties).addKeys(keys).addProperties(properties);
		}

		PropertiesInfo(List<EdmProperty.Builder> props) {
			properties = props;
		}

		PropertiesInfo(List<EdmProperty.Builder> props, List<EdmNavigationProperty.Builder> nav,
				List<EdmAssociation.Builder> assoc, List<String> keys) {
			properties = props;
			navProperties = nav;
			associations = assoc;
			this.keys = keys;
		}
	}
	private void getPropertiesForClass(Class<?> typeClazz, Map<Class<?>, EdmTypeInfo> types, EdmTypeInfo typeInfo, List<org.odata4j.edm.EdmProperty.Builder> properties, List<org.odata4j.edm.EdmNavigationProperty.Builder> navProperties, List<org.odata4j.edm.EdmAssociation.Builder> associations) {
		for (Field field : typeClazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(IgnoreProperty.class) || Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
				// TODO:ignore fiels with no accessors or mutators
				continue;
			}
			EdmTypeInfo fieldTypeInfo = getEdmType(field, field.getType(), types);
			if (fieldTypeInfo.type == EdmTypeInfo.EdmTypeType.ENTITY && typeInfo.type == EdmTypeInfo.EdmTypeType.ENTITY) {
				if (fieldTypeInfo.componentTypeInfo != null) { // is Collection
					fieldTypeInfo = fieldTypeInfo.componentTypeInfo;
				}
				String startRole = typeClazz.getSimpleName() + "_" + field.getName();
				String endRole = field.getType().getSimpleName();
				String namespace = typeClazz.getPackage().getName();
				EdmAssociationEnd.Builder start = EdmAssociationEnd.newBuilder().setMultiplicity(EdmMultiplicity.MANY)
						.setType((EdmEntityType.Builder) typeInfo.builder).setRole(startRole);
				EdmAssociationEnd.Builder end = EdmAssociationEnd.newBuilder()
						.setMultiplicity(EdmMultiplicity.ZERO_TO_ONE)
						.setType((EdmEntityType.Builder) fieldTypeInfo.builder).setRole(endRole)
						.setTypeName(field.getType().getSimpleName());
				EdmAssociation.Builder assoc = EdmAssociation.newBuilder().setEnds(start, end)
						.setName(startRole + "_" + endRole).setNamespace(namespace);
				associations.add(assoc);
				EdmNavigationProperty.Builder nav = EdmNavigationProperty.newBuilder(field.getName())
						.setRelationship(assoc).setName(field.getName()).setFromTo(start, end);
				navProperties.add(nav);
			} else {
				if(fieldTypeInfo.type==EdmTypeInfo.EdmTypeType.ENTITY || fieldTypeInfo.type==EdmTypeInfo.EdmTypeType.ENTITY_COLLECTION) {
					continue;//throw new RuntimeException("Error in type: "+fieldTypeInfo.clazz.getName());
				}
				properties.add(EdmProperty.newBuilder(field.getName()).setNullable(true).setType(fieldTypeInfo.builder));
			}
		}
	}
	private void buildNonSimpleType(Class<?> typeClazz, Map<Class<?>, EdmTypeInfo> types, EdmTypeInfo typeInfo) {
		List<EdmProperty.Builder> properties = new ArrayList<EdmProperty.Builder>();
		List<EdmNavigationProperty.Builder> navProperties = new ArrayList<EdmNavigationProperty.Builder>();
		List<EdmAssociation.Builder> associations = new ArrayList<EdmAssociation.Builder>();
		getPropertiesForClass(typeClazz,types,typeInfo,properties,navProperties,associations);
		Class<?> stopClass=Object.class;
		if(typeClazz.isAnnotationPresent(Inherited.class)) {
			stopClass=typeClazz.getAnnotation(Inherited.class).stopClass();
		}
		Class<?> process=typeClazz;
		while(process.getSuperclass()!=stopClass && process!=stopClass && process.getSuperclass()!=null) {
			process=process.getSuperclass();
			getPropertiesForClass(process, types, typeInfo, properties, navProperties, associations);
		}
		if (typeInfo.type == EdmTypeInfo.EdmTypeType.COMPLEX) {
			EdmComplexType.Builder builder = (EdmComplexType.Builder) typeInfo.builder;
			builder = builder.addProperties(properties);
			typeInfo.builder = builder;
			typeInfo.props = new PropertiesInfo(properties);
			return;
		}
		EdmEntityType.Builder builder = (EdmEntityType.Builder) typeInfo.builder;
		builder = builder.setHasStream(config.isMediaEntity(typeClazz));
		builder = builder.addProperties(properties).addNavigationProperties(navProperties);
		typeInfo.props = new PropertiesInfo(properties, navProperties, associations, null);
		typeInfo.builder = builder;

	}

	private EdmNavigationProperty.Builder createNavProperty(Field p) {
		// EdmNavigationProperty.newBuilder(p.getName()).setRelationship(relationship)
		return null;
	}

	public EdmTypeInfo getEdmType(Class<?> f, Map<Class<?>, EdmTypeInfo> types) {
		return getEdmType(null, f, types);
	}

	public EdmTypeInfo getEdmType(Field field, Class<?> f, Map<Class<?>, EdmTypeInfo> types) {
		if (types.containsKey(f)) {
			return types.get(f);
		}
		EdmSimpleType<?> ret = EdmSimpleType.forJavaType(f);
		if (ret != null) {
			return new EdmTypeInfo(f, EdmSimpleType.newBuilder(ret));
		}
		EdmCollectionType.Builder b = EdmCollectionType.newBuilder();
		if (f.isArray()) {
			EdmTypeInfo component=getEdmType(f.getComponentType(), types);
			EdmType.Builder<?, ?> collection = b.setKind(CollectionKind.List).setCollectionType(
					component.builder);
			EdmTypeInfo info = new EdmTypeInfo(f, collection);
			types.put(f.getComponentType(), component);
			return info;
		} else if (Collection.class.isAssignableFrom(f)) {
			if (field == null) {
				throw new RuntimeException("Field must be supplied to dereference collection");
			}
			ParameterizedType type = ((ParameterizedType) field.getGenericType());
			Class<?> collectionType=(Class<?>) type.getActualTypeArguments()[0];
			EdmTypeInfo collectionTypeEdm = getEdmType(collectionType, types);
			EdmType.Builder<?, ?> collection = b.setKind(CollectionKind.Collection).setCollectionType(
					collectionTypeEdm.builder);

			EdmTypeInfo info = new EdmTypeInfo(f, collection, collectionTypeEdm);
			info.type = collectionTypeEdm.type;
			types.put(collectionType, collectionTypeEdm);
			return info;
		} else if (f.isEnum()) {
			// TODO:extend framework for enum support
			return new EdmTypeInfo(f, EdmSimpleType.newBuilder(EdmSimpleType.STRING));
		}

		// check recursivley do complex and entity types then create and add
		// association
		EdmTypeInfo info;
		List<String> keys = getKeys(f);
		if (keys.isEmpty()) {
			info = new EdmTypeInfo(f, EdmComplexType.newBuilder().setName(f.getSimpleName())
					.setNamespace(f.getPackage().getName()));
		} else {
			info = new EdmTypeInfo(f, EdmEntityType.newBuilder().setName(f.getSimpleName())
					.setNamespace(f.getPackage().getName()).addKeys(keys));
		}
		types.put(f, info);
		buildNonSimpleType(f, types, info);

		return info;
	}

	private List<String> getKeys(Class<?> clazz) {
		List<String> keys = new ArrayList<String>();
		for (Field p : clazz.getDeclaredFields()) {
			if (p.isAnnotationPresent(IgnoreProperty.class)) {
				continue;
			} else if (p.isAnnotationPresent(Key.class) || p.isAnnotationPresent(Id.class)) {
				keys.add(p.getName());
			}
		}
		Class<?> stopClass=Object.class;
		if(clazz.isAnnotationPresent(Inherited.class)) {
			stopClass=clazz.getAnnotation(Inherited.class).stopClass();
		}
		if(clazz.getSuperclass()!=stopClass && clazz!=stopClass && clazz.getSuperclass()!=null) {
			keys.addAll(getKeys(clazz.getSuperclass()));
		}
		return keys;
	}

	private static class EdmTypeInfo {
		private static enum EdmTypeType {
			SIMPLE, COMPLEX, ENTITY, ENTITY_COLLECTION;
		}

		private Class<?> clazz;
		private EdmType.Builder<?, ?> builder;
		private EdmTypeType type = EdmTypeType.SIMPLE;
		private PropertiesInfo props;
		private EdmTypeInfo componentTypeInfo;

		EdmTypeInfo(Class<?> clz, EdmType.Builder<?, ?> build, PropertiesInfo props) {
			builder = build;
			clazz = clz;
			this.props = props;
			if (build instanceof EdmEntityType.Builder) {
				type = EdmTypeType.ENTITY;
				if (props != null) {
					builder = props.constructBuilder((EdmEntityType.Builder) builder);
				}
			} else if (build instanceof EdmComplexType.Builder) {
				type = EdmTypeType.COMPLEX;
			}
		}

		EdmTypeInfo(Class<?> clz, EdmType.Builder<?, ?> build) {
			this(clz, build, (PropertiesInfo) null);
		}

		EdmTypeInfo(Class<?> clz, EdmType.Builder<?, ?> build, EdmTypeInfo componentType) {
			this(clz, build, (PropertiesInfo) null);
			this.componentTypeInfo = componentType;
		}

	}
}
