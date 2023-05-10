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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.request.TaskCreationRequest;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

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
	public ResponseEntity<List<Task>> getUserStoryTaskList(@PathVariable Long userStoryId) {

		if (userStoryId == null)
			throw new BadRequestException("path variable userStoryId is null");

		List<Task> tasks = taskRepo.findAllByUserStoryId(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("no tasks related to this user story are found"));

		if (tasks.get(0).getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(tasks);
	}

	@GetMapping("/list/user/{userId}")
	public ResponseEntity<List<Task>> getUserTaskList(@PathVariable Long userId) {

		if (userId == null)
			throw new BadRequestException("path variable userId is null");

		User user = SecurityContextUtil.getUser();

		if (user.getId() != userId)
			throw new ForbiddenException("you are not allowed to other projects");

		List<Task> tasks = taskRepo.findAllByAssignedUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("no tasks related to this user are found"));

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(tasks);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createTask(@RequestBody TaskCreationRequest request) {

		if (request == null || request.getUserStoryId() == null || request.getTask() == null
				|| request.getTask().getName() == null || request.getTask().getPriority() == null)
			throw new BadRequestException("request is missing all or some of its values");

		UserStory userStory = userStoryRepo.findById(request.getUserStoryId())
				.orElseThrow(() -> new ResourceNotFoundException("no parent userStory with the given id is found"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to other projects");

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
	public ResponseEntity<Void> updateDescription(@RequestParam String description, @PathVariable long id) {

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
	public ResponseEntity<Void> updatePriority(@RequestParam Byte priority, @PathVariable long id) {

		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		if (priority < 0 || priority > 255)
			throw new BadRequestException("value is too large or too small");

		task.setPriority(priority);

		taskRepo.save(task);

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PatchMapping("/update/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		if (progress == null)
			throw new BadRequestException("request holding progress is null");

		task.setProgress(progress);

		taskRepo.save(task);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/assigneduser/{id}")
	@Transactional
	public ResponseEntity<Void> updateAssignedUser(@RequestParam String name, @PathVariable long id) {

		User user = userRepo.findUserByName(name).orElse(null);

		Task task = taskRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		if (user == null) {
			task.setAssignedUser(null);
			return ResponseEntity.status(204).build();
		}

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to assign tasks to users not in your project");

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
