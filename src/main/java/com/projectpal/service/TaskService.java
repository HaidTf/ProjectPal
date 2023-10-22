package com.projectpal.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.dto.response.entity.TaskResponseDto;
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
import com.projectpal.repository.UserRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.SortValidationUtil;
import com.projectpal.utils.UserEntityAccessValidationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepo;

	private final UserStoryService userStoryService;

	private final UserRepository userRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true)
	public Task findTaskById(long taskId) {
		return taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
	}

	@Transactional(readOnly = true)
	public List<Task> findTasksByUserStoryAndProgressSet(long userStoryId, Set<Progress> progress, Sort sort) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		UserEntityAccessValidationUtil.verifyUserAccessToUserStory(authenticationContextFacadeImpl.getCurrentUser(),
				userStory);

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findAllByUserStory(userStory, sort);
		else {
			return taskRepo.findAllByUserStoryAndProgressIn(userStory, progress, sort);
		}

	}

	@Transactional(readOnly = true)
	public Page<Task> findPageByUserAndProgressSet(User user, Set<Progress> progress, Pageable pageable) {

		if (pageable.getPageSize() > MaxAllowedUtil.MAX_PAGE_SIZE)
			throw new ConflictException("Page size exceeded size limit");

		SortValidationUtil.validateSortObjectProperties(Task.ALLOWED_SORT_PROPERTIES, pageable.getSort());

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findAllByAssignedUser(user, pageable);
		else {
			return taskRepo.findAllByAssignedUserAndProgressIn(user, progress, pageable);
		}

	}

	@Transactional(readOnly = true)
	public List<TaskResponseDto> findTaskDtoListByUserStoryAndProgressSet(long userStoryId, Set<Progress> progress,
			Sort sort) {
		
		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		UserEntityAccessValidationUtil.verifyUserAccessToUserStory(authenticationContextFacadeImpl.getCurrentUser(),
				userStory);

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findTaskDtoListByUserStory(userStory, sort);
		else {
			return taskRepo.findTaskDtoListByUserStoryAndProgressIn(userStory, progress, sort);
		}
		
	}
	
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void createTask(long userStoryId, Task task) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		UserEntityAccessValidationUtil.verifyUserAccessToUserStory(authenticationContextFacadeImpl.getCurrentUser(),
				userStory);

		if (taskRepo.countByUserStoryId(userStory.getId()) > UserStory.MAX_NUMBER_OF_TASKS)
			throw new ConflictException("Maximum number of tasks allowed reached");

		task.setUserStory(userStory);

		taskRepo.save(task);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateDescription(long taskId, String description) {

		Task task = this.findTaskById(taskId);

		UserEntityAccessValidationUtil.verifyUserAccessToProjectTask(authenticationContextFacadeImpl.getCurrentUser(),
				task);

		task.setDescription(description);

		taskRepo.save(task);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updatePriority(long taskId, int priority) {

		Task task = this.findTaskById(taskId);

		UserEntityAccessValidationUtil.verifyUserAccessToProjectTask(authenticationContextFacadeImpl.getCurrentUser(),
				task);

		task.setPriority(priority);

		taskRepo.save(task);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateProgressAndReport(long taskId, Progress progress, String report) {

		Task task = this.findTaskById(taskId);

		User currentUser = authenticationContextFacadeImpl.getCurrentUser();

		UserEntityAccessValidationUtil.verifyUserAccessToProjectTask(currentUser, task);

		if (task.getProgress() == Progress.DONE)
			throw new BadRequestException("finished task's progress cant be updated after it is set to DONE");

		if (currentUser.getRole() != Role.ROLE_USER_PROJECT_OWNER
				|| currentUser.getRole() != Role.ROLE_USER_PROJECT_OPERATOR) {
			if (currentUser.getId() != task.getAssignedUser().getId())
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

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateAssignedUser(long taskId, long userId) {

		Task task = this.findTaskById(taskId);

		UserEntityAccessValidationUtil.verifyUserAccessToProjectTask(authenticationContextFacadeImpl.getCurrentUser(),
				task);

		User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

		if (user.getProject().getId() != authenticationContextFacadeImpl.getCurrentUser().getProject().getId())
			throw new ForbiddenException("The user must be in the project");

		task.setAssignedUser(user);
		taskRepo.save(task);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void removeTaskAssignedUser(long taskId) {

		Task task = this.findTaskById(taskId);

		UserEntityAccessValidationUtil.verifyUserAccessToProjectTask(authenticationContextFacadeImpl.getCurrentUser(),
				task);

		task.setAssignedUser(null);
		taskRepo.save(task);
	}

	@Transactional
	public void exitUserTasks(User user) {

		List<Task> tasks = taskRepo.findAllByAssignedUser(user);

		if (tasks.size() > 0) {

			for (Task task : tasks) {

				task.setAssignedUser(null);
				taskRepo.save(task);

			}
		}
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void deleteTask(long taskId) {

		Task task = this.findTaskById(taskId);

		UserEntityAccessValidationUtil.verifyUserAccessToProjectTask(authenticationContextFacadeImpl.getCurrentUser(),
				task);

		taskRepo.delete(task);
	}



}
