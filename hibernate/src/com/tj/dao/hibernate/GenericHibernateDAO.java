package com.tj.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.odata4j.producer.QueryInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.tj.dao.SecurityAwareDAO;
import com.tj.dao.filter.Query.QueryType;
import com.tj.exceptions.DataConflictException;
import com.tj.exceptions.IllegalRequestException;
import com.tj.producer.KeyMap;
import com.tj.security.SecurityManager;
import com.tj.security.User;

public class GenericHibernateDAO<T> implements SecurityAwareDAO<T> {
	private static final int DEFAULT_SKIP = 0;
	private static final int DEFAULT_TOP = 500;
	@Autowired(required = false)
	private SessionFactory factory;
	private Class<T> type;
	private static Session session;

	public GenericHibernateDAO(Class<T> type) {
		this.type = type;
	}

	public GenericHibernateDAO(Class<T> type, SessionFactory factory) {
		this(type);
		this.factory = factory;
	}

	@Override
	public void finalize() throws Throwable {
		try {
			super.finalize();
		} finally {
			close();
		}
	}

	private Session getSession() {
		if (session == null){// || !session.isOpen()) {
			session = factory.openSession();
		}
		return session;
	}

	public void newSession() {
		if (session != null) {
			session.close();
		}
		session = factory.openSession();
	}
	private void cascadeSave(Object entity,Set<Object> savedObjects) {
		if(entity==null || savedObjects.contains(entity)) {return;}
		if(getSession().contains(entity)) {return;}
		savedObjects.add(entity);
		ClassMetadata meta=factory.getClassMetadata(entity.getClass());
		if(meta==null) {
			return;
		}
		for (String property : meta.getPropertyNames()) {
			Object value=meta.getPropertyValue(entity, property);
			if(value==null){continue;}
			Type t=meta.getPropertyType(property);
			if(t.isEntityType()) {
				cascadeSave(value, savedObjects);
			} else if(t.isCollectionType()) {
				for(Object o : (Iterable<?>) value) {
					cascadeSave(o,savedObjects);
				}
			}
		}
		getSession().saveOrUpdate(entity);
	}
	@Override
	public T createEntity(T entity, SecurityManager<T, ?> manager, User user) {
		try {
			cascadeSave(entity,new HashSet<Object>());
			getSession().refresh(entity);
		} catch(ConstraintViolationException e) {
			throw new DataConflictException("A data contraint would be violated with ths action. The action was not completed successfully.",e);
		} catch (RuntimeException e) {
			getSession().clear();
			throw e;
		}
		return entity;
	}

	@Override
	public Collection<T> createEntityBatch(Collection<T> entities, SecurityManager<T, ?> manager, User user) {
		for (T entity : entities) {
			createEntity(entity);
		}
		return entities;
	}

	@Override
	public T deleteEntity(KeyMap entityKey, SecurityManager<T, ?> manager, User user) {
		Object key = null;
		if (entityKey.isSingleKey()) {
			key = entityKey.getSingleKey();
		}
		T object = (T) getSession().get(type, (Serializable) key);
		if (object == null) {
			return null; // throw not found exception
		}
		try {
			getSession().delete(object);
			getSession().flush();
		} catch(ConstraintViolationException e) {
			throw new DataConflictException("A data contraint would be violated with ths action. The action was not completed successfully.");
		} catch (RuntimeException e) {
			throw e;
		}
		return object;
	}

	@Override
	public Collection<T> deleteEntityBatch(Collection<KeyMap> entityKeys, SecurityManager<T, ?> manager, User user) {
		Collection<T> ret = new ArrayList<T>();
		for (KeyMap entityKey : entityKeys) {
			ret.add(deleteEntity(entityKey));
		}
		return ret;
	}

	@Override
	public Collection<T> deleteEntityBatchFilter(QueryInfo entityKeys, SecurityManager<T, ?> manager, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T updateEntity(T entity, KeyMap keys, SecurityManager<T, ?> manager, User user) {
		try {
			if(getSession().contains(entity) ) {
				cascadeSave(entity, new HashSet<>());
				getSession().flush();
				return entity;
			}
			throw new IllegalRequestException("The entity does not exist. Create the entity first.");
		} catch(ConstraintViolationException e) {
			throw new DataConflictException("A data contraint would be violated with ths action. The action was not completed successfully.");
		}

	}

	@Override
	public Collection<T> updateEntityBatch(Map<KeyMap, T> entities, SecurityManager<T, ?> manager, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<T> updateEntityBatchFilter(QueryInfo entityKeys, SecurityManager<T, ?> manager, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T getEntity(KeyMap entity, SecurityManager<T, ?> manager, User user) {
		if (entity.isSingleKey()) {
			Object obj = entity.getSingleKey();
			return (T) getSession().get(type, (Serializable) obj);
		} else {
			Criteria criteria = getSession().createCriteria(type);
			for (String key : entity.getComplexProperties()) {
				criteria.add(Restrictions.eq(key, entity.getKey(key)));
			}

			return (T) criteria.uniqueResult();
		}
	}

	@Override
	public T getSingleEntity(QueryInfo info, SecurityManager<T, ?> manager, User user) {
		info = new QueryInfo(info.inlineCount, 1, info.skip, info.filter, info.orderBy, info.skipToken,
				info.customOptions, info.expand, info.select);
		Collection<T> results = getEntities(info);
		if (results.isEmpty()) {
			return null;
		}
		return results.iterator().next();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> getEntities(QueryInfo info, SecurityManager<T, ?> manager, User user) {
		int top = DEFAULT_TOP, skip = DEFAULT_SKIP;
		if (info.top != null && info.top > 0) {
			top = info.top;
		}
		if (info.skip != null && info.skip > 0) {
			skip = info.skip;
		}
		HibernateQuery<T> q = new HibernateQuery<T>(QueryType.RETRIEVE, type, top, skip,
				(SecurityManager<T, User>) manager, user);
		if (info.orderBy != null) {
			q.setOrderBy(new HibernateOrderBy(info.orderBy));
		}
		if (info.filter != null) {
			q.getWhere().add(info.filter);
		}
		org.hibernate.Query query = q.asHibernateQuery(getSession());

		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public long countEntities(QueryInfo filters, SecurityManager<T, ?> manager, User user) {
		HibernateQuery<T> q = new HibernateQuery<T>(QueryType.COUNT, type, (SecurityManager<T, User>) manager, user);
		q.getWhere().add(filters.filter);
		Number n = (Number) q.asHibernateQuery(getSession()).uniqueResult();
		if (n == null) {
			return 0;
		}
		return n.longValue();
	}

	@Override
	public void close() {
		if (session != null && session.isOpen()) {
			session.close();
		}

	}

	@Override
	public Class<? extends T> getDAOType() {
		return type;
	}

	@Override
	public T createEntity(T entity) {
		return createEntity(entity, null, null);
	}

	@Override
	public Collection<T> createEntityBatch(Collection<T> entities) {
		return createEntityBatch(entities, null, null);
	}

	@Override
	public T deleteEntity(KeyMap entityKey) {
		return deleteEntity(entityKey, null, null);
	}

	@Override
	public Collection<T> deleteEntityBatch(Collection<KeyMap> entityKeys) {
		return deleteEntityBatch(entityKeys, null, null);
	}

	@Override
	public Collection<T> deleteEntityBatchFilter(QueryInfo entityKeys) {
		return deleteEntityBatchFilter(entityKeys, null, null);
	}

	@Override
	public T updateEntity(T entity, KeyMap entityKey) {
		return updateEntity(entity, entityKey, null, null);
	}

	@Override
	public Collection<T> updateEntityBatch(Map<KeyMap, T> entities) {
		return updateEntityBatch(entities, null, null);
	}

	@Override
	public Collection<T> updateEntityBatchFilter(QueryInfo entityKeys) {
		return updateEntityBatchFilter(entityKeys, null, null);
	}

	@Override
	public T getEntity(KeyMap entity) {
		return getEntity(entity, null, null);
	}

	@Override
	public T getSingleEntity(QueryInfo info) {
		return getSingleEntity(info, null, null);
	}

	@Override
	public Collection<T> getEntities(QueryInfo info) {
		return getEntities(info, null, null);
	}

	@Override
	public long countEntities(QueryInfo filters) {
		return countEntities(filters, null, null);
	}
}
