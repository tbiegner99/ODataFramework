package com.tj.service;

import java.util.Collection;

import org.odata4j.producer.QueryInfo;
import org.springframework.beans.factory.annotation.Autowired;

import com.tj.dao.hibernate.GenericHibernateDAO;
import com.tj.odata.service.Service;
import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.sample.model.Person;

public class PersonService implements Service<Person> {
	@Autowired
	private GenericHibernateDAO<Person> dao;

	@Override
	public Person createEntity(Class<?> type, RequestContext request, ResponseContext response, Person object) {
		return null;//dao.createEntity(object);
	}

	@Override
	public Person mergeEntity(Class<?> type, RequestContext request, ResponseContext response, Person object,
			KeyMap keys) {
		return null;// dao.mergeEntity(object, keys);
	}

	@Override
	public Person updateEntity(Class<?> type, RequestContext request, ResponseContext response, Person object,
			KeyMap keys) {
		return null;//dao.updateEntity(object, keys);
	}

	@Override
	public Person deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return null;//dao.deleteEntity(keys);
	}

	@Override
	public Person getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		return null;//dao.getEntity(keys);
	}

	@Override
	public Collection<Person> getEntities(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return null;//dao.getEntities(info);
	}

	@Override
	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return null;//(long) dao.countEntities(info);
	}

	@Override
	public Class<? extends Person> getServiceType() {
		// TODO Auto-generated method stub
		return Person.class;
	}

	@Override
	public Person linkNewEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap objectKey, String property, Object newLink) {
		// TODO Auto-generated method stub
		return null;
	}

}
