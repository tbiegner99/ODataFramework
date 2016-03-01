package com.tj.odata.service;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.tj.producer.annotations.entity.IgnoreType;
import com.tj.producer.util.ReflectionUtil;

public abstract class AbstractPackageScanService extends AbstractCompositeService {

	private static Logger log = LoggerFactory.getLogger(AbstractPackageScanService.class);

	private Set<Object> createdServices = new HashSet<Object>();
	private AutowireCapableBeanFactory beanFactory;

	public AbstractPackageScanService(String packageName, Class<? extends Annotation> marker) {
		init(Arrays.asList(packageName), marker);
	}

	public AbstractPackageScanService(Collection<String> packageNames, Class<? extends Annotation> marker) {
		init(packageNames, marker);
	}

	@SafeVarargs
	public AbstractPackageScanService(Collection<String> packageNames, Class<? extends Annotation>... markers) {
		init(packageNames, markers);
	}

	protected AbstractPackageScanService() {
	}

	protected final void init(Map<Class<?>, Service<?>> extraServices, Collection<String> packageNames,
			Class<? extends Annotation>... markers) {
		init(packageNames, markers);
		for (Class<?> c : extraServices.keySet()) {
			this.addService(c, extraServices.get(c));
		}
	}

	@SafeVarargs
	protected final void init(Collection<String> packageNames, Class<? extends Annotation>... markers) {
		super.init(ReflectionUtil.getMarkedClassesInPackage(packageNames, Arrays.asList(markers)));
		for (Class<?> service : ReflectionUtil.getSubTypesInPackages(packageNames, Service.class)) {
			if (service.isAnnotationPresent(IgnoreType.class)) {
				continue;
			}
			try {
				Service<?> s = (Service<?>) service.newInstance();
				if (beanFactory != null) {
					this.beanFactory.autowireBean(s);
				}
				Collection<Class<?>> serviceClasses = AbstractCompositeService.getServiceTypesForClass(service);
				this.addService(serviceClasses, s);
				createdServices.add(s);
			} catch (Exception e) {
				throw new RuntimeException("Cound not instantiate type: " + service.getCanonicalName(), e);
			}
		}
	}

	@Autowired
	public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		for (Object o : createdServices) {
			this.beanFactory.autowireBean(o);
		}
	}
}
