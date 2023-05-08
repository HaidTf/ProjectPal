package com.projectpal.utils;

import com.projectpal.entity.Project;
import com.projectpal.exception.ResourceNotFoundException;

public class ProjectUtil {

	//These method is only made for use in com.projectpal.controller package because of the ExceptionHandlerController that handles the exception
	
	public static Project getProjectNotNull() {
		Project project = SecurityContextUtil.getUser().getProject();
		
		if(project == null)
			throw new ResourceNotFoundException("no project is found");
		
		return project;
	}

}
