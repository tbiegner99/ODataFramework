package com.tj.producer.util;

import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;

public class QueryInfoBuilder {

	public static QueryInfo createQueryInfo(String filter, Integer top, Integer skip, String select, String orderBy) {
		return new QueryInfo(null, OptionsQueryParser.parseTop(top == null ? null : top.toString()),
				OptionsQueryParser.parseSkip(skip == null ? null : skip.toString()),
				OptionsQueryParser.parseFilter(filter), OptionsQueryParser.parseOrderBy(orderBy), null, null, null,
				OptionsQueryParser.parseSelect(select));
	}

}
