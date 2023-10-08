package com.projectpal.utils;

import com.projectpal.entity.Announcement;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.exception.ForbiddenException;

public class UserEntityAccessValidationUtil {

	public static void verifyUserAccessToEpic(User user, Epic epic) {

		if (user.getProject() != epic.getProject())
			throw new ForbiddenException("You are not allowed access to this resource");

	}

	public static void verifyUserAccessToSprint(User user, Sprint sprint) {

		if (user.getProject() != sprint.getProject())
			throw new ForbiddenException("You are not allowed access to this resource");

	}

	public static void verifyUserAccessToUserStory(User user, UserStory userStory) {

		if (user.getProject() != userStory.getEpic().getProject())
			throw new ForbiddenException("You are not allowed access to this resource");

	}

	public static void verifyUserAccessToProjectTask(User user, Task task) {

		if (user.getProject() != task.getProject())
			throw new ForbiddenException("You are not allowed access to this resource");

	}

	public static void verifyUserAccessToAnnouncement(User user, Announcement announcement) {

		if (user.getProject() != announcement.getProject())
			throw new ForbiddenException("You are not allowed access to this resource");

	}

	public static void verifyUserAccessToProjectInvitation(User user, Invitation invitation) {

		if (user.getProject() != invitation.getProject())
			throw new ForbiddenException("You are not allowed access to this resource");

	}
	
	public static void verifyUserAccessToUserInvitation(User user, Invitation invitation) {

		if (user.getId() != invitation.getInvitedUser().getId())
			throw new ForbiddenException("You are not allowed access to this resource");

	}
}
