package com.projectpal.security.context;

import com.projectpal.entity.User;

public interface AuthenticationContextFacade {

	User getCurrentUser();

}
