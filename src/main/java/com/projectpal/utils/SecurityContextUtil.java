package com.projectpal.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.exception.ResourceNotFoundException;

public class SecurityContextUtil {

	public static User getUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public static String getName() {
		return getUser().getName();
	}

	public static String getEmail() {
		return getUser().getEmail();
	}

	public static String getPassword() {
		return getUser().getPassword();
	}

	public static Project getUserProject() {
		return getUser().getProject();
	}

	public static Project getUserProjectNotNull() {
		Project project = getUserProject();

		if (project == null)
			throw new ResourceNotFoundException("User is not a project member");

		return project;
	}
}
