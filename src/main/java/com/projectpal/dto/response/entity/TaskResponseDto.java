package com.projectpal.dto.response.entity;

import java.time.LocalDate;

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

	private long id;

	private String name;

	private String description;

	private int priority;

	private Progress progress;

	private String report;

	private LocalDate creationDate;

	private UserIdAndNameDto assignedUser;

}
