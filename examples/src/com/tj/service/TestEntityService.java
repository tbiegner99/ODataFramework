package com.tj.service;

import com.tj.producer.annotations.CreateEntity;
import com.tj.producer.annotations.GetEntity;
import com.tj.sample.model.TestEntity;

public class TestEntityService {
	@GetEntity(type = TestEntity.class)
	public TestEntity getTestEntity(Integer id) {
		return new TestEntity();
	}
	@CreateEntity(type = TestEntity.class)
	public TestEntity createTestEntity(TestEntity entity) {
		entity.setName("TJ Biegner");
		return entity;
	}
}
