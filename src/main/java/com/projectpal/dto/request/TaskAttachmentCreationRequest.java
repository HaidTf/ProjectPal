package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.entity.TaskAttachment;

public final class TaskAttachmentCreationRequest {

	@JsonCreator
	public TaskAttachmentCreationRequest(TaskAttachment taskAttachment, Long taskId) {
		this.taskAttachment = taskAttachment;
		this.taskId = taskId;
	}

	private final TaskAttachment taskAttachment;

	private final Long taskId;

	public TaskAttachment getTaskAttachment() {
		return taskAttachment;
	}

	public Long getTaskId() {
		return taskId;
	}

}
