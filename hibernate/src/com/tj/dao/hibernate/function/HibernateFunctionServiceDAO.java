package com.tj.dao.hibernate.function;

import java.util.Collection;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tj.odata.functions.FunctionInfo;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.odata.functions.FunctionService;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.configuration.ProducerConfiguration;

public abstract class HibernateFunctionServiceDAO implements FunctionService {
	private Session session;

	public HibernateFunctionServiceDAO(SessionFactory factory) {
		session = factory.openSession();
	}

	@Override
	public final Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response,ProducerConfiguration cfg) {
		return callFunction(getSession(), name, parameters, request, response,cfg);
	}

	@Override
	public void finalize() throws Throwable {
		try {
			super.finalize();
		} finally {
			close();
		}

	}

	public void close() {
		if (session.isOpen()) {
			session.close();
		}
	}

	private Session getSession() {
		return session;
	}

	public abstract Object callFunction(Session session, FunctionName name, Map<String, Object> parameters,
			RequestContext request, ResponseContext response,ProducerConfiguration cfg);

	@Override
	public abstract FunctionInfo getFunctionInfo(FunctionName functionName);

	@Override
	public abstract Collection<FunctionName> getSupportedFunctions();

}
