package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.service.TaskService;
import com.projectpal.utils.ProjectMembershipValidationUtil;
import com.projectpal.utils.UserEntityAccessValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/userstories")
public class TaskController {

	@Autowired
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	private final TaskService taskService;

	@GetMapping("/tasks/{taskId}")
	public ResponseEntity<Task> getTask(@AuthenticationPrincipal User currentUser, @PathVariable long taskId) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		Task task = taskService.findTaskById(taskId);

		UserEntityAccessValidationUtil.verifyUserAccessToProjectTask(currentUser, task);

		return ResponseEntity.ok(task);

	}

	@GetMapping("/{userStoryId}/tasks")
	public ResponseEntity<ListHolderResponse<Task>> getUserStoryTaskList(@AuthenticationPrincipal User currentUser,
			@PathVariable long userStoryId,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		List<Task> tasks = taskService.findTasksByUserStoryAndProgressSet(userStoryId, progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<Task>(tasks));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/{userStoryId}/tasks")
	public ResponseEntity<Task> createTask(@PathVariable long userStoryId, @Valid @RequestBody Task task) {

		taskService.createTask(userStoryId, task);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/userstories/tasks/" + task.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(task);

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long taskId) {

		taskService.updateDescription(taskId, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/priority")
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityUpdateRequest priorityUpdateRequest,
			@PathVariable long taskId) {

		taskService.updatePriority(taskId, priorityUpdateRequest.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PatchMapping("/tasks/{taskId}/progress")
	public ResponseEntity<Void> updateProgress(
			@RequestBody @Valid TaskProgressAndReportUpdateRequest taskProgressAndReportUpdateRequest,
			@PathVariable long taskId) {

		taskService.updateProgressAndReport(taskId, taskProgressAndReportUpdateRequest.getProgress(),
				taskProgressAndReportUpdateRequest.getReport());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/assigned-user")
	public ResponseEntity<Void> updateAssignedUser(@RequestBody @Valid IdHolderRequest userIdHolder,
			@PathVariable long taskId) {

		taskService.updateAssignedUser(taskId, userIdHolder.getId());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/tasks/{taskId}/assigned-user")
	public ResponseEntity<Void> removeAssignedUser(@PathVariable long taskId) {

		taskService.removeTaskAssignedUser(taskId);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/tasks/{taskId}")
	public ResponseEntity<Void> deleteTask(@PathVariable long taskId) {

		taskService.deleteTask(taskId);

		return ResponseEntity.status(204).build();
	}
}
