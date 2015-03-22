package com.tj.odata.service;

import java.util.Collection;
import java.util.Map;

public class GenericCompositeService extends AbstractCompositeService {

	public GenericCompositeService(Collection<Service<?>> services) {
		setUp(services);
	}

	private void setUp(Collection<Service<?>> services2) {
		Map<Class<?>, Service<?>> services = getServices();
		for (Service<?> s : services2) {
			if (s instanceof CompositeService) {
				CompositeService service = (CompositeService) s;
				for (Class<?> clazz : service.getTypes()) {
					services.put(clazz, service);
				}
				continue;
			}
			Collection<Class<?>> serviceTypes = AbstractCompositeService.getServiceTypesForClass(s.getClass());
			for (Class<?> type : serviceTypes) {
				services.put(type, s);
			}

		}
	}

	@Override
	protected <T> Service<T> buildServiceForClass(Class<T> type) {
		return null;
	}

}
