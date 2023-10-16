package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvitationResponseDto {

	private final long id;

	private final long invitedUserId;

	private final long projectId;

	private final String projectName;

	private final LocalDate issueDate;

}
