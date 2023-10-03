package com.projectpal.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.PasswordUpdateRequest;
import com.projectpal.dto.response.CustomPageResponse;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.service.TaskService;
import com.projectpal.service.UserService;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/me")
public class UserController {

	@Autowired
	public UserController(TaskService taskService, UserService userService) {
		this.taskService = taskService;
		this.userService = userService;
	}

	private final UserService userService;

	private final TaskService taskService;

	@GetMapping("")
	public ResponseEntity<User> getUser() {
		User user = SecurityContextUtil.getUser();
		return ResponseEntity.ok(user);
	}

	@GetMapping("/tasks")
	public ResponseEntity<CustomPageResponse<Task>> getMyTasks(
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@PageableDefault(page = 0, size = 20, sort = "priority", direction = Direction.DESC) Pageable pageable) {

		User user = SecurityContextUtil.getUser();

		Page<Task> tasks = taskService.findPageByUserAndProgressSet(user, progress, pageable);

		return ResponseEntity.ok(new CustomPageResponse<Task>(tasks));
	}

	@PreAuthorize("!(hasRole('SUPER_ADMIN'))")
	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {

		User user = SecurityContextUtil.getUser();

		userService.updateUserPassword(user, passwordUpdateRequest.getPassword());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@DeleteMapping("/project/membership")
	public ResponseEntity<Void> exitCurrentProject() {

		User user = SecurityContextUtil.getUser();

		userService.exitUserProject(user);

		return ResponseEntity.status(204).build();

	}

}
