package com.projectpal.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.repository.TaskRepository;
import com.projectpal.utils.SortValidationUtil;

@Service
public class TaskService {

	@Autowired
	public TaskService(TaskRepository taskRepo) {
		this.taskRepo = taskRepo;
	}

	private final TaskRepository taskRepo;

	public Task findTaskById(long taskId) {
		return taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
	}

	public List<Task> findTasksByUserStoryAndProgressSet(UserStory userStory, Set<Progress> progress, Sort sort) {

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findAllByUserStory(userStory, sort);
		else {
			return taskRepo.findAllByUserStoryAndProgressList(userStory, progress, sort);
		}

	}
	
	public Page<Task> findPageByUserAndProgressSet(User user, Set<Progress> progress, Pageable pageable) {

		SortValidationUtil.validateSortObjectProperties(Task.ALLOWED_SORT_PROPERTIES, pageable.getSort());

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findAllByAssignedUser(user, pageable);
		else {
			return taskRepo.findAllByAssignedUserAndProgressList(user, progress, pageable);
		}

	}

	public void createTask(UserStory userStory, Task task) {

		if (taskRepo.countByUserStoryId(userStory.getId()) > UserStory.MAX_NUMBER_OF_TASKS)
			throw new ConflictException("Maximum number of tasks allowed reached");

		task.setUserStory(userStory);

		taskRepo.save(task);

	}

	public void updateDescription(Task task, String description) {

		task.setDescription(description);

		taskRepo.save(task);
	}

	public void updatePriority(Task task, int priority) {

		task.setPriority(priority);

		taskRepo.save(task);
	}

	public void updateProgressAndReport(User callingUser, Task task, Progress progress, String report) {

		if (task.getProgress() == Progress.DONE)
			throw new BadRequestException("finished task's progress cant be updated after it is set to DONE");

		if (callingUser.getRole() != Role.ROLE_USER_PROJECT_OWNER
				|| callingUser.getRole() != Role.ROLE_USER_PROJECT_OPERATOR) {
			if (callingUser.getId() != task.getAssignedUser().getId())
				throw new ForbiddenException("you cant update progress of tasks assigned to other users");
		}

		if (progress == Progress.DONE) {

			if (report == null || report.isBlank())
				throw new BadRequestException("You must provide a finish report when progress is set to DONE");

			task.setReport(report);
		}

		task.setProgress(progress);

		taskRepo.save(task);
	}

	public void updateAssignedUser(Task task, User user) {

		task.setAssignedUser(user);
		taskRepo.save(task);
	}

	public void removeTaskAssignedUser(Task task) {

		task.setAssignedUser(null);
		taskRepo.save(task);
	}

	public void exitUserTasks(User user) {

		Optional<List<Task>> tasks = taskRepo.findAllByAssignedUser(user);

		if (tasks.isPresent() && tasks.get().size() > 0) {

			for (Task task : tasks.get()) {

				task.setAssignedUser(null);
				taskRepo.save(task);

			}
		}
	}

	public void deleteTask(Task task) {

		taskRepo.delete(task);
	}



}
