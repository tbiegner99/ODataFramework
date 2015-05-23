package com.tj.dao.hibernate.function;

import java.util.Collection;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.tj.odata.functions.DefaultFunctionFactory;
import com.tj.odata.functions.FunctionFactory;
import com.tj.odata.functions.FunctionInfo.FunctionName;
import com.tj.odata.functions.FunctionRouter;
import com.tj.producer.RequestContext;
import com.tj.producer.ResponseContext;
import com.tj.producer.configuration.ProducerConfiguration;

public class HibernateFunctionRouterDAO extends FunctionRouter<HibernateDAOFunction> {
	private SessionFactory sessFactory;
	private Session session;

	public HibernateFunctionRouterDAO(SessionFactory sessFactory, FunctionFactory<HibernateDAOFunction> factory) {
		super(factory);
	}

	public HibernateFunctionRouterDAO(SessionFactory sessFactory, Collection<HibernateDAOFunction> functions) {
		this(sessFactory, new DefaultFunctionFactory<HibernateDAOFunction>(functions));
	}

	@Override
	public Object invoke(FunctionName name, Map<String, Object> parameters, RequestContext request,
			ResponseContext response,ProducerConfiguration cfg) {
		HibernateDAOFunction function = getFactory().getFunction(name);
		function.setSession(getSession());
		//TODO: Handle transactions here?
		return function.invoke(name, parameters, request, response,cfg);
	}

	private Session getSession() {
		if (session == null) {
			session = sessFactory.openSession();
		}
		return session;
	}

}
