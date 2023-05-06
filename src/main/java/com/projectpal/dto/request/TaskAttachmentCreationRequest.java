package com.projectpal.dto.request;

import com.projectpal.entity.TaskAttachment;

public class TaskAttachmentCreationRequest {

	public TaskAttachmentCreationRequest(TaskAttachment taskAttachment, Long taskId) {
		this.taskAttachment = taskAttachment;
		this.taskId = taskId;
	}

	private TaskAttachment taskAttachment;
	
	private Long taskId;

	public TaskAttachment getTaskAttachment() {
		return taskAttachment;
	}

	public void setTaskAttachment(TaskAttachment taskAttachment) {
		this.taskAttachment = taskAttachment;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
}
