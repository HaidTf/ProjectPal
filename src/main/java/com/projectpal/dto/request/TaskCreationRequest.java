package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.entity.Task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public final class TaskCreationRequest {

	@JsonCreator
	public TaskCreationRequest(Task task, Long userStoryId) {
		this.task = task;
		this.userStoryId = userStoryId;
	}

	@Valid
	private final Task task;

	@NotNull
	private final Long userStoryId;

	public Task getTask() {
		return task;
	}

	public Long getUserStoryId() {
		return userStoryId;
	}

}
