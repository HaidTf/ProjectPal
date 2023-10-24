package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import com.projectpal.dto.response.entity.common.UserIdAndNameDto;

import lombok.Getter;

@Getter
public class SentInvitationResponseDto {

	public SentInvitationResponseDto(long id, LocalDate issueDate, long invitedUserId, String invitedUserName) {
		this.id = id;
		this.issueDate = issueDate;
		this.invitedUser = new UserIdAndNameDto(invitedUserId, invitedUserName);
	}

	private final long id;

	private final LocalDate issueDate;

	private final UserIdAndNameDto invitedUser;
}
