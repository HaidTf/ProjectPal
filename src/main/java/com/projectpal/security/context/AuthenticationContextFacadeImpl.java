package com.projectpal.security.context;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projectpal.entity.User;

@Service
public class AuthenticationContextFacadeImpl implements AuthenticationContextFacade {

	@Override
	public User getCurrentUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
