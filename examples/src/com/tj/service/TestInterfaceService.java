package com.tj.service;

import java.util.ArrayList;
import java.util.Collection;

import org.odata4j.producer.QueryInfo;

import com.tj.odata.service.Service;
import com.tj.producer.KeyMap;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.sample.model.TestEntity;

public class TestInterfaceService implements Service<TestEntity> {

	@Override
	public TestEntity createEntity(Class<?> type, RequestContext request, ResponseContext response, TestEntity object) {
		return object;
	}

	@Override
	public TestEntity mergeEntity(Class<?> type, RequestContext request, ResponseContext response, TestEntity object,
			KeyMap keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestEntity updateEntity(Class<?> type, RequestContext request, ResponseContext response, TestEntity object,
			KeyMap keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestEntity deleteEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TestEntity getEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<TestEntity> getEntities(Class<?> type, RequestContext request, ResponseContext response,
			KeyMap keys, QueryInfo info) {
		Collection<TestEntity> ret = new ArrayList<TestEntity>();
		for (int i = 1; i <= 10; i++) {
			TestEntity t = new TestEntity();
			t.setId(i, "" + ('A' + i - 1));

			ret.add(t);
		}
		return ret;
	}

	@Override
	public Long getEntitiesCount(Class<?> type, RequestContext request, ResponseContext response, KeyMap keys,
			QueryInfo info) {
		return 10L;
	}

	@Override
	public Class<? extends TestEntity> getServiceType() {
		// TODO Auto-generated method stub
		return TestEntity.class;
	}

	@Override
	public TestEntity linkNewEntity(Class<?> type, RequestContext request, ResponseContext response, KeyMap objectKey, String property, Object newLink) {
		// TODO Auto-generated method stub
		return null;
	}

}
