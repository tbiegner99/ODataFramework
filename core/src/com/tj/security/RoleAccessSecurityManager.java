package com.tj.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.SecurityContext;

import com.tj.dao.filter.Filter;
import com.tj.exceptions.NoLoginException;
import com.tj.exceptions.UnauthorizedException;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.security.user.Role;
import com.tj.security.user.RoleUser;
import com.tj.security.user.UserResolver;

public abstract class RoleAccessSecurityManager implements SecurityManager<Object, RoleUser>, UserResolver<RoleUser> {
	private Set<String> allowedRoles = new HashSet<>();

	public Set<String> getAllowedRoles() {
		return allowedRoles;
	}

	public void setAllowedRoles(Set<String> allowedRoles) {
		this.allowedRoles = allowedRoles;
	}

	private boolean userIsAllowed(RoleUser user) {
		if (user == null) {
			throw new UnauthorizedException("An authorized user must be logged in to perform this action");
		}
		if (allowedRoles.contains("*")) {
			return true;
		}
		for (Role r : user.getRoles()) {
			if (allowedRoles.contains(r.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canReadEntity(Class<? extends Object> subType, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public boolean canWriteEntity(Object entity, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public boolean canDeleteEntity(Class<? extends Object> subType, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public boolean canAccessProperty(Object entity, String property, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public boolean canReadProperty(Object entity, String property, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public boolean canWriteProperty(Object entity, String property, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public boolean canUpdateProperty(Object entity, String property, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public Object getPropertyValueForCreate(Object entity, Object supplied, String property, RoleUser u,ProducerConfiguration appContext) {
		return supplied;
	}

	@Override
	public Object getPropertyValueForUpdate(Object entity, Object supplied, String property, RoleUser u,ProducerConfiguration appContext) {
		return supplied;
	}

	@Override
	public Object getPropertyValueForRead(Object entity, Object supplied, String property, RoleUser u,ProducerConfiguration appContext) {
		return supplied;
	}

	@Override
	public Collection<Filter> getUserLevelFilters(Class<Object> clazz, RoleUser u,ProducerConfiguration appContext) {
		return new ArrayList<Filter>();
	}

	@Override
	public boolean canUpdateEntity(Object entity, RoleUser u,ProducerConfiguration appContext) {
		return userIsAllowed(u);
	}

	@Override
	public abstract RoleUser getUser(HttpServletRequest request, SecurityContext context) throws NoLoginException;

	@Override
	public Map<String, Object> getFilterParameters(Class<Object> clazz, RoleUser u,ProducerConfiguration appContext) {
		return new HashMap<String, Object>();
	}

}
