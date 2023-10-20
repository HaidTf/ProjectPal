package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class UserInvitationResponseDto {

	public UserInvitationResponseDto(long id, LocalDate issueDate, long projectId, String name) {
		this.id = id;
		this.issueDate = issueDate;
		this.project = new ProjectIdAndNameDto(projectId, name);
	}

	private final long id;

	private final LocalDate issueDate;

	private final ProjectIdAndNameDto project;
}
