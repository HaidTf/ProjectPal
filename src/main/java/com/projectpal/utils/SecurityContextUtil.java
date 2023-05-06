package com.projectpal.utils;

import org.springframework.security.core.context.SecurityContextHolder;


import com.projectpal.entity.User;




public class SecurityContextUtil  {
	
	public static User getUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	public static String getName(){
		return getUser().getName();
	}
	public static String getEmail(){
		return getUser().getEmail();
	}
	public static String getPassword(){
		return getUser().getPassword();
	}
	
	
}
