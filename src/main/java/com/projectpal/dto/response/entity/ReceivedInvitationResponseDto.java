package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import com.projectpal.dto.response.entity.common.ProjectIdAndNameDto;

import lombok.Getter;

@Getter
public class ReceivedInvitationResponseDto {

	public ReceivedInvitationResponseDto(long id, LocalDate issueDate, long projectId, String name) {
		this.id = id;
		this.issueDate = issueDate;
		this.project = new ProjectIdAndNameDto(projectId, name);
	}

	private final long id;

	private final LocalDate issueDate;

	private final ProjectIdAndNameDto project;
}
