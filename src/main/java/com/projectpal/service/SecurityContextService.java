package com.projectpal.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projectpal.entity.User;



@Service
public class SecurityContextService  {

	public SecurityContextService() {
		this.auth = SecurityContextHolder.getContext().getAuthentication();
	
	}

	private final Authentication auth;
	
	public User getUser() {
		return (User) auth.getPrincipal();
	}
	public String getName(){
		return getUser().getName();
	}
	public String getEmail(){
		return getUser().getEmail();
	}
	public String getPassword(){
		return getUser().getPassword();
	}
	
	
}
