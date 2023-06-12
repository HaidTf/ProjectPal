package com.projectpal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.request.PriorityParameterRequest;
import com.projectpal.dto.request.TaskCreationRequest;
import com.projectpal.dto.request.TaskProgressUpdateRequest;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/task")
public class TaskController {
	@Autowired
	public TaskController(TaskRepository taskRepo, UserStoryRepository userStoryRepo, UserRepository userRepo) {
		this.taskRepo = taskRepo;
		this.userStoryRepo = userStoryRepo;
		this.userRepo = userRepo;
	}

	private final UserRepository userRepo;

	private final TaskRepository taskRepo;

	private final UserStoryRepository userStoryRepo;

	@GetMapping("/list/userstory/{userStoryId}")
	public ResponseEntity<List<Task>> getUserStoryTaskList(@PathVariable long userStoryId) {

		List<Task> tasks = taskRepo.findAllByUserStoryId(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("no tasks related to this user story are found"));

		if (tasks.get(0).getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(tasks);
	}

	@GetMapping("/list/user/all")
	public ResponseEntity<List<Task>> getUserTaskList() {

		User user = SecurityContextUtil.getUser();

		List<Task> tasks = taskRepo.findAllByAssignedUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("no tasks related to this user are found"));

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(tasks);
	}

	@GetMapping("/list/user")
	public ResponseEntity<List<Task>> getUserTaskTodoOrInProgressList() {

		User user = SecurityContextUtil.getUser();

		List<Task> tasks = taskRepo.findAllByAssignedUserAndProgressNot(user, Progress.DONE).orElseThrow(
				() -> new ResourceNotFoundException("no todo or inprogress tasks related to this user are found"));

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(tasks);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createTask(@Valid @RequestBody TaskCreationRequest request) {

		UserStory userStory = userStoryRepo.findById(request.getUserStoryId())
				.orElseThrow(() -> new ResourceNotFoundException("no parent userStory with the given id is found"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to other projects");

		MaxAllowedUtil.checkMaxAllowedOfTask(taskRepo.countByUserStoryId(userStory.getId()));

		Task task = request.getTask();

		task.setUserStory(userStory);

		taskRepo.save(task);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/task").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/description/{id}")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long id) {

		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		task.setDescription(description);

		taskRepo.save(task);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/priority/{id}")
	@Transactional
	public ResponseEntity<Void> updatePriority(/* Request Parameter */ @Valid PriorityParameterRequest priorityHolder,
			@PathVariable long id) {

		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		task.setPriority(priorityHolder.getPriority());

		taskRepo.save(task);

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PatchMapping("/update/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@Valid TaskProgressUpdateRequest request, @PathVariable long id) {

		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		if (task.getProgress() == Progress.DONE)
			throw new BadRequestException("finished task's progress cant be updated after it is set to DONE");

		User user = SecurityContextUtil.getUser();

		if (user.getRole() != Role.ROLE_USER_PROJECT_OWNER || user.getRole() != Role.ROLE_USER_PROJECT_OPERATOR) {
			if (user.getId() != task.getAssignedUser().getId())
				throw new ForbiddenException("you cant update progress of tasks assigned to other users");
		}

		if (request.getProgress() == Progress.DONE) {

			if (request.getReport().isBlank() || request.getReport() == null)
				throw new BadRequestException("you must provide a finish report when progress is done");

			task.setReport(request.getReport());
		}

		task.setProgress(request.getProgress());

		taskRepo.save(task);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/assigneduser/{taskId}")
	@Transactional
	public ResponseEntity<Void> updateAssignedUser(/* Request Parameter */ @Nullable String name,
			@PathVariable long taskId) {

		Task task = taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		if (name == null) {
			task.setAssignedUser(null);
			taskRepo.save(task);
			return ResponseEntity.status(204).build();
		}

		User user = userRepo.findUserByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("user with specified id not found"));

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to assign tasks to users not in your project");

		task.setAssignedUser(user);
		taskRepo.save(task);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteTask(@PathVariable long id) {

		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		taskRepo.delete(task);

		return ResponseEntity.status(204).build();
	}
}
