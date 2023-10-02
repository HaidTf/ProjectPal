package com.projectpal.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.projectpal.dto.request.RoleUpdateRequest;
import com.projectpal.dto.response.CustomPageResponse;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.service.ProjectService;
import com.projectpal.service.UserService;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	public ProjectController(ProjectService projectService, UserService userService) {
		this.projectService = projectService;
		this.userService = userService;
	}

	private final ProjectService projectService;

	private final UserService userService;

	@GetMapping("")
	public ResponseEntity<Project> getProject() {

		Project project = ProjectUtil.getProjectNotNull();

		projectService.updateProjectLastAccessedDate(project);

		return ResponseEntity.ok(project);
	}

	@GetMapping("/users")
	public ResponseEntity<CustomPageResponse<User>> getProjectMembers(@RequestParam(required = false) Role role,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size) {

		Project project = ProjectUtil.getProjectNotNull();

		MaxAllowedUtil.checkMaxAllowedPageSize(size);

		Page<User> users = userService.findAllByProjectAndRole(project, role, page, size);

		return ResponseEntity.ok(new CustomPageResponse<User>(users));

	}

	@PreAuthorize("hasAnyRole('USER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PostMapping("")
	@Transactional
	public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {

		User user = SecurityContextUtil.getUser();

		projectService.createProjectAndSetOwner(project, user);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(project);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest) {

		Project project = ProjectUtil.getProjectNotNull();

		projectService.updateProjectDescription(project, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','ADMIN')")
	@PatchMapping("/users/{userId}/role")
	@Transactional
	public ResponseEntity<Void> setUserProjectRole(@PathVariable long userId,
			@RequestBody @Valid RoleUpdateRequest roleUpdateRequest) {

		User user = userService.findUserById(userId);

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("the user must be in the project");

		if (SecurityContextUtil.getUser().getId() == userId)
			throw new BadRequestException("you can not change your own role");

		if (roleUpdateRequest.getRole() != Role.ROLE_USER_PROJECT_PARTICIPATOR
				|| roleUpdateRequest.getRole() != Role.ROLE_USER_PROJECT_OPERATOR)
			throw new BadRequestException("You are not allowed to set this role");

		userService.updateUserRole(user, roleUpdateRequest.getRole());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/users/{userId}/membership")
	@Transactional
	public ResponseEntity<Void> removeUserFromProject(@PathVariable long userId) {

		User user = userService.findUserById(userId);

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("the user must be in the project");

		if (SecurityContextUtil.getUser().getId() == userId)
			throw new BadRequestException("you cant remove yourself from the project through here");

		userService.exitUserProject(user);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','ADMIN')")
	@DeleteMapping("")
	@Transactional
	public ResponseEntity<Void> deleteProject() {

		Project project = ProjectUtil.getProjectNotNull();

		projectService.deleteProject(project);

		return ResponseEntity.status(204).build();

	}

}
