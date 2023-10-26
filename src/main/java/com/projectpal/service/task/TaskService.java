package com.projectpal.service.task;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.projectpal.dto.response.entity.TaskResponseDto;
import com.projectpal.entity.Project;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;

public interface TaskService {

	public Task findTaskById(long taskId);

	public Task findTaskByIdAndProject(long taskId, Project project);

	public TaskResponseDto findTaskDtoByIdAndProject(long taskId, Project project);

	public List<Task> findTasksByUserStoryAndProgressSet(long userStoryId, Set<Progress> progress, Sort sort);

	public Page<Task> findPageByUserAndProgressSet(User user, Set<Progress> progress, Pageable pageable);

	public List<TaskResponseDto> findTaskDtoListByUserStoryAndProgressSet(long userStoryId, Set<Progress> progress,
			Sort sort);

	public void createTask(long userStoryId, Task task);

	public void updateDescription(long taskId, String description);

	public void updatePriority(long taskId, int priority);

	public void updateProgressAndReport(long taskId, Progress progress, String report);

	public void updateAssignedUser(long taskId, long userId);

	public void removeTaskAssignedUser(long taskId);

	public void exitUserTasks(User user);

	public void deleteTask(long taskId);

}
