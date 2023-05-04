package com.projectpal.utils;

import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;

public class ProjectUtil {

	//These method is only made for use in com.projectpal.controller package because of the ExceptionHandlerController that handles the exception
	
	public static void onlyProjectOwnerAllowed() {
		
		User mustBeProjectOwner = SecurityContextUtil.getUser();

		if (mustBeProjectOwner.getProject()==null)
			throw new ResourceNotFoundException("no project is found");
		
		if (mustBeProjectOwner.getId() != mustBeProjectOwner.getProject().getOwner().getId())
			throw new ForbiddenException("you are not the project owner");
	}
	
	public static Project getProjectNotNull() {
		Project project = SecurityContextUtil.getUser().getProject();
		
		if(project == null)
			throw new ResourceNotFoundException("no project is found");
		
		return project;
	}

}
