package com.projectpal.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

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

import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.RoleUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.CacheServiceProjectAddOn;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	public ProjectController(ProjectRepository projectRepo, UserRepository userRepo, TaskRepository taskRepo,
			CacheServiceProjectAddOn cacheServiceProjectAddOn) {
		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
		this.taskRepo = taskRepo;
		this.cacheServiceProjectAddOn = cacheServiceProjectAddOn;
	}

	private final ProjectRepository projectRepo;

	private final UserRepository userRepo;

	private final TaskRepository taskRepo;

	private final CacheServiceProjectAddOn cacheServiceProjectAddOn;

	@GetMapping("")
	public ResponseEntity<Project> getProject() {

		Project project = ProjectUtil.getProjectNotNull();

		project.setLastAccessedDate(LocalDate.now());

		projectRepo.save(project);

		return ResponseEntity.ok(project);
	}

	@GetMapping("/users")
	public ResponseEntity<ListHolderResponse<User>> getProjectMembers() {

		Project project = ProjectUtil.getProjectNotNull();

		List<User> users = userRepo.findAllByProject(project).orElse(new ArrayList<User>(0));

		return ResponseEntity.ok(new ListHolderResponse<User>(users));

	}

	@PreAuthorize("hasAnyRole('USER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PostMapping("")
	@Transactional
	public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {

		User user = SecurityContextUtil.getUser();

		user.setRole(Role.ROLE_USER_PROJECT_OWNER);
		user.setProject(project);

		project.setOwner(user);

		projectRepo.save(project);
		userRepo.save(user);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/projects").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(project);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest) {

		Project project = ProjectUtil.getProjectNotNull();

		project.setDescription(descriptionUpdateRequest.getDescription());

		projectRepo.save(project);

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','ADMIN')")
	@PatchMapping("/users/{userId}/role")
	@Transactional
	public ResponseEntity<Void> setUserProjectRole(@PathVariable long userId,
			@RequestBody @Valid RoleUpdateRequest roleUpdateRequest) {

		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("no user with this id is found"));

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("the user must be in the project");

		if (SecurityContextUtil.getUser().getId() == userId)
			throw new BadRequestException("you can not change your own role");

		if (roleUpdateRequest.getRole() != Role.ROLE_USER_PROJECT_PARTICIPATOR
				|| roleUpdateRequest.getRole() != Role.ROLE_USER_PROJECT_OPERATOR)
			throw new BadRequestException("You are not allowed to set this role");

		user.setRole(roleUpdateRequest.getRole());

		userRepo.save(user);

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/users/{userId}/membership")
	@Transactional
	public ResponseEntity<Void> removeUserFromProject(@PathVariable long userId) {

		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("no user with this id is found"));

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("the user must be in the project");

		if (SecurityContextUtil.getUser().getId() == userId)
			throw new BadRequestException("you cant remove yourself from the project through here");

		user.setProject(null);

		user.setRole(Role.ROLE_USER);

		userRepo.save(user);

		Optional<List<Task>> tasks = taskRepo.findAllByAssignedUser(user);

		if (tasks.isPresent() && tasks.get().size() > 0) {

			for (Task task : tasks.get()) {
				task.setAssignedUser(null);
				taskRepo.save(task);
			}

		}
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','ADMIN')")
	@DeleteMapping("")
	@Transactional
	public ResponseEntity<Void> deleteProject() {

		Project project = ProjectUtil.getProjectNotNull();

		Optional<List<User>> projectUsers = userRepo.findAllByProject(project);

		cacheServiceProjectAddOn.DeleteEntitiesInCacheOnProjectDeletion(project);

		projectRepo.delete(project);

		if (projectUsers.isPresent() && projectUsers.get().size() > 0) {

			for (User projectUser : projectUsers.get()) {
				projectUser.setRole(Role.ROLE_USER);
				userRepo.save(projectUser);
			}

		}

		User owner = SecurityContextUtil.getUser();
		owner.setRole(Role.ROLE_USER);
		userRepo.save(owner);

		return ResponseEntity.status(204).build();

	}

}
