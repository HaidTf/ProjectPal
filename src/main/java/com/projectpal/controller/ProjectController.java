package com.projectpal.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
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

import com.projectpal.dto.mapper.ProjectMapper;
import com.projectpal.dto.request.DescriptionDto;
import com.projectpal.dto.request.RoleDto;
import com.projectpal.dto.request.entity.ProjectCreationDto;
import com.projectpal.dto.response.CustomPageResponse;
import com.projectpal.dto.response.entity.ProjectResponseDto;
import com.projectpal.dto.response.entity.ProjectMemberResponseDto;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.service.project.ProjectService;
import com.projectpal.service.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	private final UserService userService;

	private final ProjectMapper projectMapper;

	@GetMapping("")
	public ResponseEntity<ProjectResponseDto> getProject(@AuthenticationPrincipal User currentUser) {

		Project project = currentUser.getOptionalOfProject()
				.orElseThrow(() -> new ResourceNotFoundException("User is not in a project"));

		return ResponseEntity.ok(projectService.findProjectDtoById(project.getId()));
	}

	@GetMapping("/users")
	public ResponseEntity<CustomPageResponse<ProjectMemberResponseDto>> getProjectMembers(
			@AuthenticationPrincipal User currentUser, @RequestParam(required = false) Role role,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "20") int size) {

		Project project = currentUser.getOptionalOfProject()
				.orElseThrow(() -> new ResourceNotFoundException("User is not in a project"));

		Page<ProjectMemberResponseDto> users = userService.findProjectMembersDtoListByProjectAndRole(project, role,
				page, size);

		return ResponseEntity.ok(new CustomPageResponse<ProjectMemberResponseDto>(users));

	}

	@PreAuthorize("hasAnyRole('USER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PostMapping("")
	public ResponseEntity<Project> createProject(@AuthenticationPrincipal User currentUser,
			@Valid @RequestBody ProjectCreationDto projectCreationDto) {

		Project project = projectMapper.toProject(projectCreationDto);

		projectService.createProjectAndSetOwner(project, currentUser);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(project);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/description")
	public ResponseEntity<Void> updateDescription(@AuthenticationPrincipal User currentUser,
			@RequestBody DescriptionDto descriptionUpdateRequest) {

		Project project = currentUser.getProject();

		projectService.updateProjectDescription(project, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','ADMIN')")
	@PatchMapping("/users/{userId}/role")
	public ResponseEntity<Void> setOtherUserProjectRole(@AuthenticationPrincipal User currentUser,
			@PathVariable long otherUserId, @RequestBody @Valid RoleDto roleUpdateRequest) {

		userService.updateUserProjectRole(currentUser, otherUserId, roleUpdateRequest.getRole());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/users/{userId}/membership")
	public ResponseEntity<Void> removeOtherUserFromProject(@AuthenticationPrincipal User currentUser,
			@PathVariable long otherUserId) {

		projectService.removeUserFromCurrentUserProject(currentUser, otherUserId);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','ADMIN')")
	@DeleteMapping("")
	public ResponseEntity<Void> deleteProject(@AuthenticationPrincipal User currentUser) {

		Project project = currentUser.getProject();

		projectService.deleteProject(project);

		return ResponseEntity.status(204).build();

	}

}
