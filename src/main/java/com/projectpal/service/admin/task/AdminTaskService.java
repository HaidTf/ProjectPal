package com.projectpal.service.admin.task;

import com.projectpal.entity.Task;

public interface AdminTaskService {

	Task findTaskById(long taskId);

	void deleteTask(long taskId);

}
