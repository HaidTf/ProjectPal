package com.projectpal.service.task;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.dto.response.entity.TaskResponseDto;
import com.projectpal.entity.DBConstants;
import com.projectpal.entity.Project;
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
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.validation.PageValidator;
import com.projectpal.validation.SortObjectValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

	private final TaskRepository taskRepo;

	private final UserStoryRepository userStoryRepo;

	private final UserRepository userRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true)
	@Override
	public Task findTaskById(long taskId) {
		return taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
	}

	@Transactional(readOnly = true)
	@Override
	public Task findTaskByIdAndProject(long taskId, Project project) {
		return taskRepo.findByIdAndProject(taskId, project)
				.orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
	}

	@Transactional(readOnly = true)
	@Override
	public TaskResponseDto findTaskDtoByIdAndProject(long taskId, Project project) {
		return taskRepo.findTaskDtoByIdAndProject(taskId, project)
				.orElseThrow(() -> new ResourceNotFoundException("Task does not exist"));
	}

	@Transactional(readOnly = true)
	@Override
	public List<Task> findTasksByUserStoryAndProgressSet(long userStoryId, Set<Progress> progress, Sort sort) {

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory not found"));

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findAllByUserStory(userStory, sort);
		else {
			return taskRepo.findAllByUserStoryAndProgressIn(userStory, progress, sort);
		}

	}

	@Transactional(readOnly = true)
	@Override
	public Page<Task> findPageByUserAndProgressSet(User user, Set<Progress> progress, Pageable pageable) {

		PageValidator.validatePageable(pageable);

		SortObjectValidator.validateSortObjectProperties(Task.ALLOWED_SORT_PROPERTIES, pageable.getSort());

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findAllByAssignedUser(user, pageable);
		else {
			return taskRepo.findAllByAssignedUserAndProgressIn(user, progress, pageable);
		}

	}

	@Transactional(readOnly = true)
	@Override
	public List<TaskResponseDto> findTaskDtoListByUserStoryAndProgressSet(long userStoryId, Set<Progress> progress,
			Sort sort) {

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory not found"));

		if (progress.size() == 0 || progress.size() == 3)
			return taskRepo.findTaskDtoListByUserStory(userStory, sort);
		else {
			return taskRepo.findTaskDtoListByUserStoryAndProgressIn(userStory, progress, sort);
		}

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void createTask(long userStoryId, Task task) {

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory not found"));

		if (taskRepo.countByUserStoryId(userStory.getId()) > DBConstants.MAX_NUMBER_OF_TASKS)
			throw new ConflictException("Maximum number of tasks allowed reached");

		task.setProgress(Progress.TODO);

		task.setUserStory(userStory);

		taskRepo.save(task);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void updateDescription(long taskId, String description) {

		Task task = taskRepo.findByIdAndProject(taskId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));

		task.setDescription(description);

		taskRepo.save(task);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void updatePriority(long taskId, int priority) {

		Task task = taskRepo.findByIdAndProject(taskId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));

		task.setPriority(priority);

		taskRepo.save(task);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void updateProgressAndReport(long taskId, Progress progress, String report) {

		User currentUser = authenticationContextFacadeImpl.getCurrentUser();

		Task task = taskRepo.findByIdAndProject(taskId, currentUser.getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));

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
	@Override
	public void updateAssignedUser(long taskId, long userId) {

		Task task = taskRepo.findByIdAndProject(taskId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));

		User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

		if (user.getProject().getId() != authenticationContextFacadeImpl.getCurrentUser().getProject().getId())
			throw new ForbiddenException("The user must be in the project");

		task.setAssignedUser(user);
		taskRepo.save(task);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void removeTaskAssignedUser(long taskId) {

		Task task = taskRepo.findByIdAndProject(taskId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));

		task.setAssignedUser(null);
		taskRepo.save(task);
	}

	@Transactional
	@Override
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
	@Override
	public void deleteTask(long taskId) {

		Task task = taskRepo.findByIdAndProject(taskId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));

		taskRepo.delete(task);
	}

}
