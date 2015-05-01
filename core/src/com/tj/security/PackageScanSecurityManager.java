package com.tj.security;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.tj.producer.annotations.SecurityManagerFor;
import com.tj.producer.annotations.entity.IgnoreType;
import com.tj.producer.util.ReflectionUtil;


public class PackageScanSecurityManager extends GenericSecurityManager{

	private AutowireCapableBeanFactory beanFactory;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PackageScanSecurityManager(String... scannablePackages) {
		Set<Class<? extends SecurityManager>> managers=ReflectionUtil.getSubTypesInPackages(Arrays.asList(scannablePackages), SecurityManager.class);
		for(Class<? extends SecurityManager> c : managers) {
			try {
				if(c.isAnnotationPresent(IgnoreType.class)) {continue;}
				Class<?> type=null;
				for(Type interfaze : c.getGenericInterfaces()) {
					if(interfaze==SecurityManager.class) {
						type=(Class<? extends SecurityManager>) ((ParameterizedType)interfaze).getActualTypeArguments()[0];
					}
				}

				if(c.isAnnotationPresent(SecurityManagerFor.class)) {
					type=c.getAnnotation(SecurityManagerFor.class).type();
				}
				if(type==null) {
					continue;
				}
				addSecurityManagerForClass(type, c.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				continue;
			}
		}
	}
	public <T> PackageScanSecurityManager(Map<Class<? extends T>,SecurityManager<T,?>> addtl, String... scannablePackages) {
		this(scannablePackages);
		for(Class<? extends T> clazz : addtl.keySet()) {
			addSecurityManagerForClass(clazz, addtl.get(clazz));
		}

	}
	@Autowired
	public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		for (SecurityManager<?, ?> o : getAllSecurityManagers()) {
			this.beanFactory.autowireBean(o);
		}
	}
}
