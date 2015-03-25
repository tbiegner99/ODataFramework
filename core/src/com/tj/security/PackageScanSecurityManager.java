package com.tj.security;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

import com.tj.producer.annotations.SecurityManagerFor;
import com.tj.producer.annotations.entity.IgnoreType;
import com.tj.producer.util.ReflectionUtil;


public class PackageScanSecurityManager extends GenericSecurityManager{

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
				if(type==null) {
					continue;
				}
				if(c.isAnnotationPresent(SecurityManagerFor.class)) {
					type=c.getAnnotation(SecurityManagerFor.class).type();
				}
				addSecurityManagerForClass(type, c.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				//TODO: log
				continue;
			}
		}
	}
}
