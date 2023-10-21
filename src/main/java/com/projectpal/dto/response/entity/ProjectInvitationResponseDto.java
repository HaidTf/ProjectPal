package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class ProjectInvitationResponseDto {

	public ProjectInvitationResponseDto(long id, LocalDate issueDate, long invitedUserId, String invitedUserName) {
		this.id = id;
		this.issueDate = issueDate;
		this.invitedUser = new UserIdAndNameDto(invitedUserId, invitedUserName);
	}

	private final long id;

	private final LocalDate issueDate;

	private final UserIdAndNameDto invitedUser;
}
