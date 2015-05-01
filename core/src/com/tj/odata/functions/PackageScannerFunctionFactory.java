package com.tj.odata.functions;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.tj.producer.util.ReflectionUtil;

public class PackageScannerFunctionFactory extends DefaultFunctionFactory<Function> {
	private AutowireCapableBeanFactory beanFactory;

	public PackageScannerFunctionFactory(String pack, Class<?>... ignoreClasses) {
		init(Arrays.asList(pack), Arrays.asList(ignoreClasses));
	}

	public PackageScannerFunctionFactory(Collection<String> packs, Collection<Class<?>> ignoreClasses) {
		init(packs, ignoreClasses);
	}

	public PackageScannerFunctionFactory(Collection<String> packs, Collection<Function> additionalFunctions,
			Collection<Class<?>> ignoreClasses) {
		init(packs, ignoreClasses);
		for (Function f : additionalFunctions) {
			addItem(f);
		}
	}

	public PackageScannerFunctionFactory(String... packs) {
		init(Arrays.asList(packs), null);
	}

	protected void init(Collection<String> packages, Collection<Class<?>> ignore) {
		for (Class<?> function : ReflectionUtil.getSubTypesInPackages(packages, Function.class, ignore)) {
			try {
				Function instance = (Function) function.newInstance();
				if (beanFactory != null) {
					beanFactory.autowireBean(instance);
				}
				addItem(instance);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Unable to instatiate class: " + function.getName()
						+ ". Ensure the file is ignored or has a public no-arg constructor.", e);
			}
		}
	}

	@Autowired
	public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		for (Object o : getFunctions()) {
			beanFactory.autowireBean(o);
		}
	}
}
