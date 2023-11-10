package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

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

import com.projectpal.dto.mapper.TaskMapper;
import com.projectpal.dto.request.DescriptionDto;
import com.projectpal.dto.request.IdDto;
import com.projectpal.dto.request.PriorityDto;
import com.projectpal.dto.request.ProgressAndReportDto;
import com.projectpal.dto.request.entity.TaskCreationDto;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.dto.response.entity.TaskResponseDto;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.service.task.TaskService;
import com.projectpal.validation.ProjectMembershipValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/userstories")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

	private final TaskService taskService;

	private final TaskMapper taskMapper;

	@GetMapping("/tasks/{taskId}")
	public ResponseEntity<TaskResponseDto> getTask(@AuthenticationPrincipal User currentUser,
			@PathVariable long taskId) {

		log.debug("API:GET/api/userstories/tasks/{} invoked", taskId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		TaskResponseDto taskDto = taskService.findTaskDtoByIdAndProject(taskId, currentUser.getProject());

		return ResponseEntity.ok(taskDto);

	}

	@GetMapping("/{userStoryId}/tasks")
	public ResponseEntity<ListHolderResponse<TaskResponseDto>> getUserStoryTaskList(
			@AuthenticationPrincipal User currentUser, @PathVariable long userStoryId,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		log.debug("API:GET/api/userstories/{}/tasks invoked", userStoryId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		List<TaskResponseDto> tasks = taskService.findTaskDtoListByUserStoryAndProgressSet(userStoryId, progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<TaskResponseDto>(tasks));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/{userStoryId}/tasks")
	public ResponseEntity<Task> createTask(@PathVariable long userStoryId,
			@Valid @RequestBody TaskCreationDto taskCreationDto) {

		log.debug("API:POST/api/userstories/{}/tasks invoked", userStoryId);

		Task task = taskMapper.toTask(taskCreationDto);

		taskService.createTask(userStoryId, task);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/userstories/tasks/" + task.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(task);

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionDto descriptionUpdateRequest,
			@PathVariable long taskId) {

		log.debug("API:PATCH/api/userstories/tasks/{}/description invoked", taskId);

		taskService.updateDescription(taskId, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/priority")
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityDto priorityUpdateRequest,
			@PathVariable long taskId) {

		log.debug("API:GET/api/userstories/tasks/{}/priority invoked", taskId);

		taskService.updatePriority(taskId, priorityUpdateRequest.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PatchMapping("/tasks/{taskId}/progress")
	public ResponseEntity<Void> updateProgress(
			@RequestBody @Valid ProgressAndReportDto taskProgressAndReportUpdateRequest, @PathVariable long taskId) {

		log.debug("API:PATCH/api/userstories/tasks/{}/progress invoked", taskId);

		taskService.updateProgressAndReport(taskId, taskProgressAndReportUpdateRequest.getProgress(),
				taskProgressAndReportUpdateRequest.getReport());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/tasks/{taskId}/assigned-user")
	public ResponseEntity<Void> updateAssignedUser(@RequestBody @Valid IdDto userIdHolder, @PathVariable long taskId) {

		log.debug("API:PATCH/api/userstories/tasks/{}/assigned-user invoked", taskId);

		taskService.updateAssignedUser(taskId, userIdHolder.getId());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/tasks/{taskId}/assigned-user")
	public ResponseEntity<Void> removeAssignedUser(@PathVariable long taskId) {

		log.debug("API:DELETE/api/userstories/tasks/{}/assigned-user invoked", taskId);

		taskService.removeTaskAssignedUser(taskId);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/tasks/{taskId}")
	public ResponseEntity<Void> deleteTask(@PathVariable long taskId) {

		log.debug("API:DELETE/api/userstories/tasks/{} invoked", taskId);

		taskService.deleteTask(taskId);

		return ResponseEntity.status(204).build();
	}
}
