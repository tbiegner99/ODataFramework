package com.tj.dao.hibernate;

import java.util.Collection;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tj.odata.functions.FunctionInfo;
import com.tj.odata.functions.FunctionService;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;

public abstract class HibernateFunctionServiceDAO implements FunctionService {
	private Session session;

	public HibernateFunctionServiceDAO(SessionFactory factory) {
		session = factory.openSession();
	}

	@Override
	public final Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response) {
		return callFunction(getSession(), name, parameters, request, response);
	}

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
			RequestContext request, ResponseContext response);

	@Override
	public abstract FunctionInfo getFunctionInfo(FunctionName functionName);

	@Override
	public abstract Collection<FunctionName> getSupportedFunctions();

}
