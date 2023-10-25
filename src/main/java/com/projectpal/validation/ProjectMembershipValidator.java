package com.projectpal.validation;

import com.projectpal.entity.User;
import com.projectpal.exception.ResourceNotFoundException;

public class ProjectMembershipValidator {

	public static void verifyUserProjectMembership(User user) {
		
		if (user.getProject() == null)
			throw new ResourceNotFoundException("User is not in a project");
	}

}
