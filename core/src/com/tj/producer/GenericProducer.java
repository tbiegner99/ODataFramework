package com.tj.producer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.odata4j.core.OCollection;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityId;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OExtension;
import org.odata4j.core.OFunctionParameter;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.core.OSimpleObject;
import org.odata4j.core.OStructuralObject;
import org.odata4j.edm.EdmCollectionType;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmFunctionParameter;
import org.odata4j.edm.EdmGenerator;
import org.odata4j.edm.EdmNavigationProperty;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmPropertyBase;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.edm.EdmStructuralType;
import org.odata4j.edm.EdmType;
import org.odata4j.exceptions.BadRequestException;
import org.odata4j.producer.BaseResponse;
import org.odata4j.producer.CountResponse;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityIdResponse;
import org.odata4j.producer.EntityQueryInfo;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.InlineCount;
import org.odata4j.producer.ODataContext;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.OMediaLinkExtensions;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.Responses;
import org.odata4j.producer.edm.MetadataProducer;

import com.tj.datastructures.PropertyPath;
import com.tj.exceptions.IllegalAccessException;
import com.tj.exceptions.IllegalOperationException;
import com.tj.exceptions.IllegalRequestException;
import com.tj.exceptions.NotFoundException;
import com.tj.odata.extensions.EdmJavaTypeConverter;
import com.tj.odata.functions.FunctionInfo;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.odata.service.Service;
import com.tj.producer.application.ApplicationMediaLinkExtensions;
import com.tj.producer.configuration.AnnotationProducerConfiguration;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.producer.configuration.ProducerConfiguration.Action;
import com.tj.producer.configuration.ServiceProducerConfiguration;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.FunctionSecurityManager;
import com.tj.security.User;

public class GenericProducer implements ODataProducer {
	private ProducerConfiguration cfg; // TODO: static
	private EdmGenerator edm; // TODO: static
	private HttpServletRequest requestContext;
	private ResponseContext responseContext = new ResponseContext.DefaultResponseContext();
	private CompositeSecurityManager securityManager;
	private User requestUser;
	private EdmDataServices metadata;

	public GenericProducer(List<Object> services) {
		cfg = new AnnotationProducerConfiguration(services);
		edm = new GenericEdmGenerator(cfg);
		metadata = edm.generateEdm(null).build();
	}

	public GenericProducer(Object... services) {
		this(Arrays.asList(services));
	}

	public GenericProducer(Service<?>... services) {
		cfg = new ServiceProducerConfiguration(Arrays.asList(services));
		edm = new GenericEdmGenerator(cfg);
		metadata = edm.generateEdm(null).build();
	}

	public GenericProducer(ProducerConfiguration config) {
		cfg = config;
		edm = new GenericEdmGenerator(config);
		metadata = edm.generateEdm(null).build();
	}

	public GenericProducer(ProducerConfiguration config, EdmDataServices metadata) {
		cfg = config;
		edm = new GenericEdmGenerator(config);
		this.metadata = metadata;
	}

	public GenericProducer(HttpServletRequest request, ResponseContext response,
			CompositeSecurityManager securityContext, User user, ProducerConfiguration config) {
		cfg = config;
		edm = new GenericEdmGenerator(config);
		requestContext = request;
		responseContext = response;
		this.requestUser = user;
		securityManager = securityContext;
		metadata = edm.generateEdm(null).build();
	}

	public GenericProducer(HttpServletRequest request, ResponseContext response,
			CompositeSecurityManager securityContext, User user, ProducerConfiguration config, EdmDataServices metadata) {
		cfg = config;
		edm = new GenericEdmGenerator(config);
		requestContext = request;
		responseContext = response;
		this.requestUser = user;
		securityManager = securityContext;
		this.metadata = metadata;
	}

	private boolean canPerformEntityAcion(ODataContext odataContext, Action action, Class<?> entityType, Object entity) {
		if (securityManager == null) {
			return true;
		}
		switch (action) {
			case GET:
			case LIST:
			case COUNT:
				return securityManager.canReadEntity(entityType, requestUser);
			case CREATE:
				return securityManager.canWriteEntity(entity, requestUser);
			case DELETE:
				return securityManager.canDeleteEntity(entityType, requestUser);
			case PATCH:
			case UPDATE:
				return securityManager.canUpdateEntity(entity, requestUser);
			default:
				throw new IllegalOperationException("Operation not defined: " + action);

		}
	}

	private boolean canPerformPropertyAcion(ODataContext odataContext, Action action, Class<?> entityType,
			String propName) {
		if (securityManager == null) {
			return true;
		}
		switch (action) {
			case GET:
			case LIST:
			case COUNT:
				return securityManager.canReadProperty(entityType, propName, requestUser);
			case CREATE:
				return securityManager.canWriteProperty(entityType, propName, requestUser);
			case DELETE:
				return securityManager.canDeleteEntity(entityType, requestUser);
			case PATCH:
			case UPDATE:
				return securityManager.canUpdateProperty(entityType, propName, requestUser);
			default:
				throw new IllegalOperationException("Operation not defined: " + action);

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TExtension extends OExtension<ODataProducer>> TExtension findExtension(Class<TExtension> clazz) {
		if (clazz == OMediaLinkExtensions.class) {
			return (TExtension) new ApplicationMediaLinkExtensions(cfg, securityManager, requestUser);
		}
		return null;
	}

	@Override
	public EdmDataServices getMetadata() {
		return metadata;
	}

	@Override
	public MetadataProducer getMetadataProducer() {
		return new MetadataProducer(this, null);
	}

	@Override
	public EntitiesResponse getEntities(ODataContext ocontext, String entitySetName, QueryInfo queryInfo) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		if (type == null) {
			throw new NotFoundException();
		}
		if (!canPerformEntityAcion(ocontext, Action.LIST, type, null)) {
			throw new IllegalAccessException("user does not have permission to perform this action");
		}
		RequestContext context = RequestContext.createRequestContext(ocontext, queryInfo, type, requestContext,
				securityManager, requestUser);
		Object o = cfg.invoke(entitySetName, Action.LIST, context, responseContext);
		Collection<? extends Object> objects = (Collection<? extends Object>) o;
		List<OEntity> ret = new ArrayList<OEntity>();
		for (Object item : objects) {
			OEntity entity = OEntityConverter.createOEntity(getMetadata(), item, queryInfo);
			ret.add(entity);
		}
		Integer skipToken = (queryInfo.skip == null ? ret.size() : queryInfo.skip + ret.size());
		EdmEntitySet set = getMetadata().getEdmEntitySet(entitySetName);
		Integer inlineCount = null;
		if (queryInfo.inlineCount == InlineCount.ALLPAGES) {
			inlineCount = (Integer) cfg.invoke(entitySetName, Action.COUNT, context, responseContext);
		}
		return Responses.entities(ret, set, inlineCount, "" + skipToken);
	}

	@Override
	public CountResponse getEntitiesCount(ODataContext ocontext, String entitySetName, QueryInfo queryInfo) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		if (type == null) {
			throw new NotFoundException();
		}
		if (!canPerformEntityAcion(ocontext, Action.COUNT, type, null)) {
			throw new IllegalAccessException("user does not have permission to perform this action");
		}
		RequestContext context = RequestContext.createRequestContext(ocontext, queryInfo, type, requestContext,
				securityManager, requestUser);
		Object o = cfg.invoke(entitySetName, Action.COUNT, context, responseContext);
		return Responses.count((Long) o);
	}

	@Override
	public EntityResponse getEntity(ODataContext ocontext, String entitySetName, OEntityKey entityKey,
			EntityQueryInfo queryInfo) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		if (type == null) {
			throw new NotFoundException();
		}
		RequestContext context = RequestContext.createRequestContext(ocontext, entityKey, queryInfo, type,
				requestContext, securityManager, requestUser);
		if (!canPerformEntityAcion(ocontext, Action.GET, type, null)) {
			throw new IllegalAccessException("user does not have permission to perform this action");
		}
		Object o = cfg.invoke(entitySetName, Action.GET, context, responseContext);
		if (o == null) {
			throw new NotFoundException("No entity found with this id.");
		}
		OEntity response = OEntityConverter.createOEntity(getMetadata(), o, queryInfo);
		return Responses.entity(response);
	}

	public BaseResponse getSimpleProp(EdmProperty edmProp, Object prop) {
		EdmType type = edmProp.getType();
		OProperty<?> property = null;
		if (EdmCollectionType.class.isAssignableFrom(type.getClass())) {
			property = OProperties.collection(edmProp.getName(), (EdmCollectionType) type,
					OEntityConverter.getCollection(getMetadata(), edmProp, prop));

		} else if (EdmComplexType.class.isAssignableFrom(type.getClass())) {
			property = OProperties.complex(edmProp.getName(), (EdmComplexType) type, OEntityConverter
					.getPropertiesList(getMetadata(), null, (EdmStructuralType) type, prop,
							PropertyPath.getEmptyPropertyPath()));
		} else if (type.isSimple()) {
			property = OProperties.simple(edmProp.getName(), (EdmSimpleType<?>) type, prop);
		}
		if (property == null) {
			throw new IllegalOperationException("Type not implemented: " + type.getFullyQualifiedTypeName());
		}
		return Responses.property(property);
	}

	@Override
	public BaseResponse getNavProperty(ODataContext ocontext, String entitySetName, OEntityKey entityKey,
			String navPropPath, QueryInfo queryInfo) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		if (type == null) {
			throw new NotFoundException();
		}
		RequestContext context = RequestContext.createRequestContext(ocontext, entityKey, queryInfo, type,
						requestContext, securityManager, requestUser);
		Object o = cfg.invoke(entitySetName, Action.GET, context, responseContext);
		if (o == null) {
			throw new BadRequestException("No entity found with this id.");
		}
		PropertyPath path=new PropertyPath(Arrays.asList(navPropPath));
		String navProp=null;
		PropertyDescriptor pd=null;
		Object prop=null;
		try {
			String[] components=navPropPath.split("/");
			//TODO: handle nested properties (and keys? for v3?)
			for(String component : components) {
				navProp=component;
				if(!path.nextComponentContains(component)) {
					throw new IllegalRequestException("Invalid component '"+component+"' in path: "+navPropPath);
				}
				path=path.getSubPath(component);
				if (!canPerformPropertyAcion(ocontext, Action.GET, type, navProp)) {
					throw new IllegalAccessException("User does not have permission to perform this action");
				}
				pd = new PropertyDescriptor(navProp, o.getClass());
				prop = pd.getReadMethod().invoke(o);
				if(prop==null && !path.isLeaf()) {
					//throw new NotFound
				}
				o=prop;
			}
			EdmDataServices data = getMetadata();
			EdmPropertyBase edmProp = data.findEdmProperty(navProp);
			if(prop==null) {
				return Responses.property(OProperties.null_(edmProp.getName(), EdmSimpleType.STRING)); //its null do we care what type it is?
			}
			if (!EdmNavigationProperty.class.isAssignableFrom(edmProp.getClass())) {

				return getSimpleProp((EdmProperty) edmProp, prop);
			}
			EdmNavigationProperty property = (EdmNavigationProperty) edmProp;
			EdmEntityType edmType = property.getToRole().getType();
			if (!Collection.class.isAssignableFrom(pd.getPropertyType())) {
				return Responses.entity(OEntityConverter.createOEntity(getMetadata(), prop,pd.getPropertyType(), queryInfo));
			}
			int size = 0;
			List<OEntity> collection = new ArrayList<OEntity>();
			for (Object item : (Iterable<?>) prop) {
				collection.add(OEntityConverter.createOEntity(getMetadata(), item, queryInfo));
				size++;
			}
			return Responses.entities(collection, data.findEdmEntitySet(edmType.getName()), size, null);
		} catch(IllegalRequestException e) {
			throw e;
		}catch (IntrospectionException e) {
			throw new NotFoundException("Property not found: " + navProp);
		} catch (Exception e) {
			throw new IllegalRequestException("Path is not valid: "+navPropPath);
		}
	}

	@Override
	public CountResponse getNavPropertyCount(ODataContext context, String entitySetName, OEntityKey entityKey,
			String navProp, QueryInfo queryInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
	}

	@Override
	public EntityResponse createEntity(ODataContext ocontext, String entitySetName, OEntity entity) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		if (type == null) {
			throw new NotFoundException();
		}
		Object entityObject = OEntityConverter.oEntityToObject(entity, type);
		if (!canPerformEntityAcion(ocontext, Action.CREATE, type, entityObject)) {
			throw new IllegalAccessException("user does not have permission to perform this action");
		}
		RequestContext context = RequestContext.createRequestContext(ocontext, entityObject, type, requestContext,
				securityManager, requestUser);
		Object o = cfg.invoke(entitySetName, Action.CREATE, context, responseContext);
		OEntity response = OEntityConverter.createOEntity(getMetadata(), o);
		return Responses.entity(response);
	}

	@Override
	public EntityResponse createEntity(ODataContext ocontext, String entitySetName, OEntityKey entityKey,
			String navProp, OEntity entity) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		if (type == null) {
			throw new NotFoundException();
		}
		Object entityObject = OEntityConverter.oEntityToObject(entity, type);
		if (!canPerformEntityAcion(ocontext, Action.CREATE, type, entityObject)) {
			throw new IllegalAccessException("user does not have permission to perform this action");
		}
		RequestContext context = RequestContext.createRequestContext(ocontext, entityObject, type, requestContext,
				securityManager, requestUser);
		Object o = cfg.invoke(entitySetName, Action.CREATE, context, responseContext);
		OEntity response = OEntityConverter.createOEntity(getMetadata(), o);
		return Responses.entity(response);
	}

	@Override
	public void deleteEntity(ODataContext ocontext, String entitySetName, OEntityKey entityKey) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		if (type == null) {
			throw new NotFoundException();
		}
		RequestContext context = RequestContext.createRequestContext(ocontext, entityKey, type, requestContext,
				securityManager, requestUser);
		if (!canPerformEntityAcion(ocontext, Action.DELETE, type, null)) {
			throw new IllegalAccessException("user does not have permission to perform this action");
		}
		cfg.invoke(entitySetName, Action.DELETE, context, responseContext);
		return;

	}

	@Override
	public void mergeEntity(ODataContext ocontext, String entitySetName, OEntity entity) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		Object entityObject = OEntityConverter.oEntityToObject(entity, type);
		RequestContext context = RequestContext.createRequestContext(ocontext, entityObject, type, requestContext,
				securityManager, requestUser);
		cfg.invoke(entitySetName, Action.PATCH, context, responseContext);
		return;

	}

	@Override
	public void updateEntity(ODataContext ocontext, String entitySetName, OEntity entity) {
		Class<?> type = cfg.getEntitySetClass(entitySetName);
		Object entityObject = OEntityConverter.oEntityToObject(entity, type);
		RequestContext context = RequestContext.createRequestContext(ocontext, entityObject, type, requestContext,
				securityManager, requestUser);
		cfg.invoke(entitySetName, Action.UPDATE, context, responseContext);
		return;

	}

	@Override
	public EntityIdResponse getLinks(ODataContext context, OEntityId sourceEntity, String targetNavProp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createLink(ODataContext context, OEntityId sourceEntity, String targetNavProp, OEntityId targetEntity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateLink(ODataContext context, OEntityId sourceEntity, String targetNavProp,
			OEntityKey oldTargetEntityKey, OEntityId newTargetEntity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteLink(ODataContext context, OEntityId sourceEntity, String targetNavProp,
			OEntityKey targetEntityKey) {
		// TODO Auto-generated method stub

	}

	private Map<String, EdmFunctionParameter> indexParameters(Collection<EdmFunctionParameter> params) {
		Map<String, EdmFunctionParameter> ret = new HashMap<String, EdmFunctionParameter>();
		for (EdmFunctionParameter p : params) {
			ret.put(p.getName(), p);
		}
		return ret;
	}

	private Object getParameterValue(String paramName, EdmSimpleType<?> type, Object actualValue, FunctionInfo info) {
		Class<?> targetClass = info.getParameters().get(paramName);
		return EdmJavaTypeConverter.convertToClass(type, actualValue, targetClass);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public BaseResponse callFunction(ODataContext context, EdmFunctionImport name,
			Map<String, OFunctionParameter> params, QueryInfo queryInfo) {
		FunctionName functionName = new FunctionName(name.getName(), name.getHttpMethod());
		FunctionInfo functionInfo = cfg.getFunctionInfo(functionName);
		FunctionSecurityManager security = securityManager.getSecurityManagerForFunction(functionName);
		Map<String, EdmFunctionParameter> paramsIndex = indexParameters(name.getParameters());
		if (security != null && security.canCallFunction(requestUser)) {
			throw new IllegalAccessException("User does not have permission to call this function");
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (Map.Entry<String, OFunctionParameter> vals : params.entrySet()) {
			EdmFunctionParameter param = paramsIndex.get(vals.getKey());
			OFunctionParameter p = vals.getValue();
			if (p == null && param != null) {
				if (!param.isNullable()) {
					throw new BadRequestException("Missing required parameter " + param.getName() + " of type "
							+ param.getType().getFullyQualifiedTypeName());
				}
				parameters.put(vals.getKey(), null);
				continue;
			}
			if (p.getType().isSimple()) {
				Object value = ((OSimpleObject<?>) p.getValue()).getValue();
				value = getParameterValue(param.getName(), (EdmSimpleType<?>) param.getType(), value, functionInfo);
				parameters.put(vals.getKey(), value);
			} else {
				Class<?> type = cfg.getEntitySetClass(p.getType().getFullyQualifiedTypeName());
				Object obj = OEntityConverter.oEntityToObject((OStructuralObject) p.getValue(), type);
				parameters.put(vals.getKey(), obj);
			}
		}
		RequestContext request = RequestContext.createRequestContext(context, queryInfo, Object.class, requestContext,
				null, requestUser);
		if (security != null) {
			// use security manager to translate reuested parameters into allowed parameters
			for (String key : parameters.keySet()) {
				parameters.put(key, security.getFunctionArgument(parameters.get(key), requestUser));
			}
		}
		Object ret = cfg.invoke(functionName, parameters, request, responseContext);
		if (security != null) {
			// use security manager to translate returned value into allowed return object
			ret = security.getReturnValue(ret, requestUser);
		}
		if (name.getReturnType().isSimple()) {
			return Responses.simple((EdmSimpleType<?>) name.getReturnType(), ret);
		} else if (name.getReturnType() instanceof EdmCollectionType) {
			OCollection<?> items = OEntityConverter.getCollection(getMetadata(),
					((EdmCollectionType) name.getReturnType()), ret);
			EdmType type = ((EdmCollectionType) name.getReturnType()).getItemType();
			if (type instanceof EdmEntityType) {
				EdmEntitySet set = getMetadata().findEdmEntitySet(((EdmEntityType) type).getName());
				return Responses.collection(items, set, null, null, ((EdmEntityType) type).getName());
			}
			return Responses.collection(items);
		} else if (name.getReturnType() instanceof EdmComplexType) {
			return Responses.complexObject(OEntityConverter.createComplexObject(getMetadata(), ret,
					(EdmComplexType) name.getReturnType(), queryInfo), "value");
		}
		// if (name.getReturnType() != o.getClass()) {
		// throw new UnexpectedException("");
		// }
		return Responses.entity(OEntityConverter.createOEntity(getMetadata(), ret, queryInfo));
	}
}
