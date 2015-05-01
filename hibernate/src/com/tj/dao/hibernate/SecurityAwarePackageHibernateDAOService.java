package com.tj.dao.hibernate;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tj.odata.service.Service;

@Transactional(propagation=Propagation.REQUIRED)
@org.springframework.stereotype.Service
public class SecurityAwarePackageHibernateDAOService extends  AbstractHibernatePackageScanService {

	public SecurityAwarePackageHibernateDAOService(String packageName, Class<? extends Annotation> marker, SessionFactory fact) {
		super(fact);
		init(Arrays.asList(packageName), marker);

	}

	public SecurityAwarePackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker,
			SessionFactory fact) {
		super(fact);
		init(packageNames, marker);
	}

	public SecurityAwarePackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker) {
		super(null);
		init(packageNames, marker);
	}

	public SecurityAwarePackageHibernateDAOService(SessionFactory fact, Collection<String> packageNames,
			Class<? extends Annotation>... markers) {
		super(fact);
		init(packageNames, markers);
	}

	public SecurityAwarePackageHibernateDAOService(SessionFactory fact, Collection<String> packageNames,
			Class<? extends Annotation> marker, Map<Class<?>, Service<?>> extras) {
		super(fact);
		init(extras, packageNames, marker);
	}

	public SecurityAwarePackageHibernateDAOService(Collection<String> packageNames, Class<? extends Annotation> marker,
			Map<Class<?>, Service<?>> extras) {
		super(null);
		init(extras, packageNames, marker);
	}

	@Override
	protected <T> Service<T> buildServiceForClass(Class<T> type) {
		if (this.getSessionFactory() == null) {
			return new SecurityAwareHibernateDAOService<T>(type);
		}
		return new SecurityAwareHibernateDAOService<T>(type, getSessionFactory());
	}



}

