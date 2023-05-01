package com.projectpal.utils;

import com.projectpal.entity.User;
import com.projectpal.exception.ForbiddenException;

public class ProjectUtil {

	//This method is only made for use in com.projectpal.controller package because of the ExceptionHandlerController that handles the exception
	public static void onlyProjectOwnerAllowed() {
		
		User mustBeProjectOwner = SecurityContextUtil.getUser();

		if (mustBeProjectOwner.getId() != mustBeProjectOwner.getProject().getOwner().getId())
			throw new ForbiddenException("you are not the project owner");
	}

}
