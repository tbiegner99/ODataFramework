package com.tj.dao.hibernate.function;

import java.util.Map;

import org.hibernate.Session;

import com.tj.odata.functions.FunctionInfo;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.configuration.ProducerConfiguration;

public abstract class BaseHibernateDAOFunction implements HibernateDAOFunction {

	private Session session;

	@Override
	public abstract FunctionInfo getAliases();

	@Override
	public abstract Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response,ProducerConfiguration cfg);

	@Override
	public void setSession(Session session) {
		this.session = session;

	}

	public Session getSession() {
		return session;
	}

}
