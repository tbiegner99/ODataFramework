package com.tj.dao.hibernate;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.tj.odata.service.AbstractPackageScanService;
import com.tj.odata.service.Service;

public class PackageHibernateDAOService extends AbstractPackageScanService {
	private SessionFactory factory;
	public PackageHibernateDAOService(String packageName, Class<? extends Annotation> marker, SessionFactory fact) {
		factory = fact;
		init(Arrays.asList(packageName), marker);

	}

	public PackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker,
			SessionFactory fact) {
		factory = fact;
		init(packageNames, marker);
	}

	public PackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker) {
		init(packageNames, marker);
	}

	public PackageHibernateDAOService(SessionFactory fact, Collection<String> packageNames,
			Class<? extends Annotation>... markers) {
		factory = fact;
		init(packageNames, markers);
	}

	public PackageHibernateDAOService(SessionFactory fact, Collection<String> packageNames,
			Class<? extends Annotation> marker, Map<Class<?>, Service<?>> extras) {
		factory = fact;
		init(extras, packageNames, marker);
	}

	public PackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker,
			Map<Class<?>, Service<?>> extras) {
		init(extras, packageNames, marker);
	}

	@Override
	protected <T> Service<T> buildServiceForClass(Class<T> type) {
		if (factory == null) {
			return new GenericHibernateDAOService<T>(type);
		}
		return new GenericHibernateDAOService<T>(type, factory);
	}

}
