package com.tj.sample.model;

import java.util.Date;
import java.util.List;

import com.tj.producer.annotations.entity.Key;

public class TestEntity {
	/*
	 * @Key private Integer id;
	 *
	 * @Key private String key2;
	 */
	/**/
	@Key
	private EncapsulatedKey id;
	/**/
	private TestComplexType ctype;
	private String name;
	private Date born;
	private List<Integer> ints;
	private String[] strings;
	private TestEntity selfReference;

	// private Map<String, TestComplexType> ctypes;

	/*
	 * public String getKey2() { return key2; }
	 *
	 * public void setKey2(String key2) { this.key2 = key2; }
	 */

	public TestComplexType getCtype() {
		return ctype;
	}

	public void setCtype(TestComplexType ctype) {
		this.ctype = ctype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBorn() {
		return born;
	}

	public void setBorn(Date born) {
		this.born = born;
	}

	public EncapsulatedKey getUniqueKey() {
		return getId();
	}

	public EncapsulatedKey getId() {
		return id;
	}

	public void setId(EncapsulatedKey id) {
		this.id = id;
	}

	/*
	 * private Integer getId() { return id; }
	 *
	 * public void setId(Integer id) { this.id = id; }
	 */
	static class EncapsulatedKey {
		Integer id;
		String key2;
	}

	public void setId(int i, String string) {
		EncapsulatedKey key = new EncapsulatedKey();
		key.id = i;
		key.key2 = string;
		setId(key);

	}
}
