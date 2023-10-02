package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
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

import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.IdHolderRequest;
import com.projectpal.dto.request.PriorityUpdateRequest;
import com.projectpal.dto.request.TaskProgressAndReportUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.service.TaskService;
import com.projectpal.service.UserService;
import com.projectpal.service.UserStoryService;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/userstories")
public class TaskController {

	@Autowired
	public TaskController(TaskService taskService, UserStoryService userStoryService, UserService userService) {
		this.taskService = taskService;
		this.userStoryService = userStoryService;
		this.userService = userService;
	}

	private final TaskService taskService;

	private final UserStoryService userStoryService;

	private final UserService userService;

	@GetMapping("/tasks/{taskId}")
	public ResponseEntity<Task> getTask(@PathVariable long taskId) {

		Project project = ProjectUtil.getProjectNotNull();

		Task task = taskService.findTaskById(taskId);

		if (task.getProject().getId() != project.getId())
			throw new ForbiddenException("You are not allowed access to other projects");

		return ResponseEntity.ok(task);

	}

	@GetMapping("/{userStoryId}/tasks")
	public ResponseEntity<ListHolderResponse<Task>> getUserStoryTaskList(@PathVariable long userStoryId,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		List<Task> tasks = taskService.findTasksByUserStoryAndProgressSet(userStory, progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<Task>(tasks));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/{userStoryId}/tasks")
	@Transactional
	public ResponseEntity<Task> createTask(@PathVariable long userStoryId, @Valid @RequestBody Task task) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to acccess other projects");

		taskService.createTask(userStory, task);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/userstories/tasks/" + task.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(task);

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long taskId) {

		Task task = taskService.findTaskById(taskId);

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		taskService.updateDescription(task, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/priority")
	@Transactional
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityUpdateRequest priorityUpdateRequest,
			@PathVariable long taskId) {

		Task task = taskService.findTaskById(taskId);

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		taskService.updatePriority(task, priorityUpdateRequest.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PatchMapping("/tasks/{taskId}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(
			@RequestBody @Valid TaskProgressAndReportUpdateRequest taskProgressAndReportUpdateRequest,
			@PathVariable long taskId) {

		Task task = taskService.findTaskById(taskId);

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		User user = SecurityContextUtil.getUser();

		taskService.updateProgressAndReport(user, task, taskProgressAndReportUpdateRequest.getProgress(),
				taskProgressAndReportUpdateRequest.getReport());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/assigned-user")
	@Transactional
	public ResponseEntity<Void> updateAssignedUser(@RequestBody @Valid IdHolderRequest userIdHolder,
			@PathVariable long taskId) {

		Task task = taskService.findTaskById(taskId);

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		User user = userService.findUserById(userIdHolder.getId());

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("the user must be in the project");

		taskService.updateAssignedUser(task,user);
		
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/tasks/{taskId}/assigned-user")
	@Transactional
	public ResponseEntity<Void> removeAssignedUser(@PathVariable long taskId) {

		Task task = taskService.findTaskById(taskId);

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		taskService.removeTaskAssignedUser(task);
		
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/tasks/{taskId}")
	@Transactional
	public ResponseEntity<Void> deleteTask(@PathVariable long taskId) {

		Task task = taskService.findTaskById(taskId);

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		taskService.deleteTask(task);

		return ResponseEntity.status(204).build();
	}
}
