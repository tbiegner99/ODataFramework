package com.tj.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * Builds info that can be used for an odata request. tHIs
 * is useful for in app calls to sevices where the request is normalized to
 * an odata request.
 * 
 * @author TJ Biegner
 *
 */
public class RequestInfoBuilder {
	private RequestInfo requestInfo;

	private RequestInfoBuilder() {
		requestInfo = new RequestInfo();
	}

	public RequestInfo build() {
		return requestInfo;
	}

	public RequestInfoBuilder setOrderProperty(String orderProp) {
		requestInfo.orderProperty = orderProp;
		return this;
	}

	public RequestInfoBuilder addSelectFilter(String... selectProp) {
		requestInfo.selectProps.addAll(Arrays.asList(selectProp));
		return this;
	}

	public RequestInfoBuilder setOrderDirection(RequestInfo.Order orderDirection) {
		requestInfo.orderDirection = orderDirection;
		return this;
	}

	public RequestInfoBuilder setMaxResults(Integer max) {
		requestInfo.maxResults = max;
		return this;
	}

	public RequestInfoBuilder setRecordStart(Integer start) {
		requestInfo.startAt = start;
		return this;
	}

	public static RequestInfoBuilder newInstance() {
		return new RequestInfoBuilder();
	}

	public static class RequestInfo {
		public static enum Order {
			ASCENDING, DESCENDING
		}

		private RequestInfo() {
			selectProps = new ArrayList<String>();
		}

		private List<String> selectProps;
		private String orderProperty;
		private Order orderDirection;
		private Integer startAt;
		private Integer maxResults;

		public String[] getSelectProperties() {
			String[] ret = new String[selectProps.size()];
			return selectProps.toArray(ret);
		}

		public String getOrderProperty() {
			return orderProperty;
		}

		public void setOrderProperty(String orderProperty) {
			this.orderProperty = orderProperty;

		}

		public Order getOrderDirection() {
			return orderDirection;
		}

		public void setOrderDirection(Order orderDirection) {
			this.orderDirection = orderDirection;
		}

		public Integer getStartAt() {
			return startAt;
		}

		public void setStartAt(Integer startAt) {
			this.startAt = startAt;
		}

		public Integer getMaxResults() {
			return maxResults;
		}

		public void setMaxResults(Integer maxResults) {
			this.maxResults = maxResults;
		}

	}
}
