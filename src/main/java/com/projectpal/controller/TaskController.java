package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

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
import com.projectpal.dto.request.TaskProgressUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/userstories")
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

	@GetMapping("/tasks/{taskId}")
	public ResponseEntity<Task> getTask(@PathVariable long taskId) {

		Project project = ProjectUtil.getProjectNotNull();

		Task task = taskRepo.getReferenceById(taskId);

		try {
			if (task.getProject().getId() != project.getId())
				throw new ForbiddenException("You are not allowed access to other projects");
		} catch (EntityNotFoundException ex) {
			throw new ResourceNotFoundException("Task does not exist");
		}

		return ResponseEntity.ok(task);

	}

	@GetMapping("/{userStoryId}/tasks")
	public ResponseEntity<ListHolderResponse<Task>> getUserStoryTaskList(@PathVariable long userStoryId) {

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		List<Task> tasks = taskRepo.findAllByUserStoryId(userStoryId).orElse(new ArrayList<Task>(0));

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(new ListHolderResponse<Task>(tasks));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/{userStoryId}/tasks")
	@Transactional
	public ResponseEntity<Task> createTask(@PathVariable long userStoryId, @Valid @RequestBody Task task) {

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("no userStory with the given id is found"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to acccess other projects");

		MaxAllowedUtil.checkMaxAllowedOfTask(taskRepo.countByUserStoryId(userStory.getId()));

		task.setUserStory(userStory);

		taskRepo.save(task);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/userstories/tasks/" + task.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(task);

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long taskId) {

		Task task = taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		task.setDescription(description);

		taskRepo.save(task);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/priority")
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
	@PatchMapping("/tasks/{taskId}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid TaskProgressUpdateRequest request,
			@PathVariable long taskId) {

		Task task = taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("task not found"));

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
	@PatchMapping("/tasks/{taskId}/users")
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
	@DeleteMapping("/tasks/{taskId}")
	@Transactional
	public ResponseEntity<Void> deleteTask(@PathVariable long taskId) {

		Task task = taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("task not found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		taskRepo.delete(task);

		return ResponseEntity.status(204).build();
	}
}
