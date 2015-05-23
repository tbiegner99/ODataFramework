package com.tj.security.user;

import java.util.Collection;

public interface RoleUser extends User {
	Collection<? extends Role> getRoles();

	boolean isSuperUser();
}
