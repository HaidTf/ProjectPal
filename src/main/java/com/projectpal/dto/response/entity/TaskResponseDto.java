package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import com.projectpal.dto.response.entity.common.UserIdAndNameDto;
import com.projectpal.entity.enums.Progress;

import lombok.Getter;

@Getter
public class TaskResponseDto {

	public TaskResponseDto(long id, String name, String description, int priority, Progress progress, String report,
			LocalDate creationDate, long userId, String userName) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = progress;
		this.report = report;
		this.creationDate = creationDate;
		this.assignedUser = new UserIdAndNameDto(userId, userName);
	}

	private final long id;

	private final String name;

	private final String description;

	private final int priority;

	private final Progress progress;

	private final String report;

	private final LocalDate creationDate;

	private final UserIdAndNameDto assignedUser;

}
