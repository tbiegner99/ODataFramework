package com.tj.producer.util;

import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;

public class QueryInfoBuilder {

	private Integer top;
	private Integer skip;
	private String filter;
	private String orderBy;
	private String select;
	private String skipToken;
	private String inlineCount;
	private String expand;

	private QueryInfoBuilder() {}

	public QueryInfoBuilder setFilter(String filter) {
		this.filter=filter;
		return this;
	}

	public QueryInfoBuilder setTop(Integer top) {
		this.top = top;
		return this;
	}


	public QueryInfoBuilder setSkip(Integer skip) {
		this.skip = skip;
		return this;
	}


	public QueryInfoBuilder setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}


	public QueryInfoBuilder setSelect(String select) {
		this.select = select;
		return this;
	}


	public QueryInfoBuilder setSkipToken(String skipToken) {
		this.skipToken = skipToken;
		return this;
	}


	public QueryInfoBuilder setInlineCount(String inlineCount) {
		this.inlineCount = inlineCount;
		return this;
	}


	public QueryInfoBuilder setExpand(String expand) {
		this.expand = expand;
		return this;
	}

	public QueryInfo build() {
		return new QueryInfo(OptionsQueryParser.parseInlineCount(inlineCount),
						OptionsQueryParser.parseTop(top == null ? null : top.toString()),
						OptionsQueryParser.parseSkip(skip == null ? null : skip.toString()),
						OptionsQueryParser.parseFilter(filter),
						OptionsQueryParser.parseOrderBy(orderBy),
						OptionsQueryParser.parseSkipToken(skipToken),
						null,
						OptionsQueryParser.parseExpand(expand),
						OptionsQueryParser.parseSelect(select));
	}
	public static QueryInfoBuilder newInstance() {
		return new QueryInfoBuilder();
	}
	public static QueryInfoBuilder newInstance(QueryInfo info) {
		return new QueryInfoBuilder()
		.setInlineCount(info.inlineCount.toString())
		.setTop(info.top)
		.setSkip(info.skip)
		.setSkipToken(info.skipToken);
	}
	public static QueryInfo createQueryInfo(String filter, Integer top, Integer skip, String select, String orderBy) {
		return new QueryInfo(null, OptionsQueryParser.parseTop(top == null ? null : top.toString()),
				OptionsQueryParser.parseSkip(skip == null ? null : skip.toString()),
				OptionsQueryParser.parseFilter(filter), OptionsQueryParser.parseOrderBy(orderBy), null, null, null,
				OptionsQueryParser.parseSelect(select));
	}

}
