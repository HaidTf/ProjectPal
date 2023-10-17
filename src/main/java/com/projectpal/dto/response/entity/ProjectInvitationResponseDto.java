package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectInvitationResponseDto {

	private final long id;

	private final long invitedUserId;

	private final String invitedUserName;

	private final LocalDate issueDate;

}
