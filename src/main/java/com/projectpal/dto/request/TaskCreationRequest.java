package com.projectpal.dto.request;

import com.projectpal.entity.Task;

public class TaskCreationRequest {

	public TaskCreationRequest(Task task, Long userStoryId) {
		this.task = task;
		this.userStoryId = userStoryId;
	}

	private Task task;
	
	private Long userStoryId;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Long getUserStoryId() {
		return userStoryId;
	}

	public void setUserStoryId(Long userStoryId) {
		this.userStoryId = userStoryId;
	}
	
}
