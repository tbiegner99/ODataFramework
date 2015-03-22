package com.tj.dao.hibernate;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.tj.odata.service.AbstractPackageScanService;
import com.tj.odata.service.Service;


public class SecurityAwarePackageHibernateDAOService extends AbstractPackageScanService {
	private SessionFactory factory;
	public SecurityAwarePackageHibernateDAOService(String packageName, Class<? extends Annotation> marker, SessionFactory fact) {
		factory = fact;
		init(Arrays.asList(packageName), marker);

	}

	public SecurityAwarePackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker,
			SessionFactory fact) {
		factory = fact;
		init(packageNames, marker);
	}

	public SecurityAwarePackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker) {
		init(packageNames, marker);
	}

	public SecurityAwarePackageHibernateDAOService(SessionFactory fact, Collection<String> packageNames,
			Class<? extends Annotation>... markers) {
		factory = fact;
		init(packageNames, markers);
	}

	public SecurityAwarePackageHibernateDAOService(SessionFactory fact, Collection<String> packageNames,
			Class<? extends Annotation> marker, Map<Class<?>, Service<?>> extras) {
		factory = fact;
		init(extras, packageNames, marker);
	}

	public SecurityAwarePackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker,
			Map<Class<?>, Service<?>> extras) {
		init(extras, packageNames, marker);
	}

	@Override
	protected <T> Service<T> buildServiceForClass(Class<T> type) {
		if (factory == null) {
			return new SecurityAwareHibernateDAOService<T>(type);
		}
		return new SecurityAwareHibernateDAOService<T>(type, factory);
	}

}

