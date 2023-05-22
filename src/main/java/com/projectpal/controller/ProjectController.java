package com.projectpal.controller;

import java.net.URI;
import java.time.LocalDate;
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
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	public ProjectController(ProjectRepository projectRepo, UserRepository userRepo, TaskRepository taskRepo) {

		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
		this.taskRepo = taskRepo;
	}

	private final ProjectRepository projectRepo;

	private final UserRepository userRepo;

	private final TaskRepository taskRepo;

	@GetMapping("")
	public ResponseEntity<Project> getProject() {

		Project project = ProjectUtil.getProjectNotNull();

		project.setLastAccessedDate(LocalDate.now());

		projectRepo.save(project);
		
		return ResponseEntity.ok(project);
	}

	@PreAuthorize("hasAnyRole('USER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createProject(@RequestBody Project project) {

		if (project == null || project.getName() == null)
			throw new BadRequestException("the request body is null");

		User user = SecurityContextUtil.getUser();

		user.setRole(Role.ROLE_USER_PROJECT_OWNER);
		user.setProject(project);

		project.setOwner(user);

		projectRepo.save(project);
		userRepo.save(user);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestParam String description) {

		Project project = ProjectUtil.getProjectNotNull();

		project.setDescription(description);

		projectRepo.save(project);

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasRole('USER_PROJECT_OWNER')")
	@PatchMapping("/update/setoperator/{name}")
	@Transactional
	public ResponseEntity<Void> setProjectOperator(@PathVariable String name) {

		User user = userRepo.findUserByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("no user with the intended name is found"));

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("the user must be in the project to be set as project operator");

		if (SecurityContextUtil.getUser().getName() == name)
			throw new BadRequestException("you cant set yourself as a project operator");

		user.setRole(Role.ROLE_USER_PROJECT_OPERATOR);

		userRepo.save(user);

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/remove/user/{name}")
	@Transactional
	public ResponseEntity<Void> removeUserFromProject(@PathVariable String name) {

		User user = userRepo.findUserByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("no user with the intended name is found"));

		if (user.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed remove users from other projects");

		if (SecurityContextUtil.getUser().getName() == name)
			throw new BadRequestException("you cant remove yourself from the project through here");

		user.setProject(null);

		userRepo.save(user);

		List<Task> tasks = taskRepo.findAllByAssignedUser(user).orElse(null);

		if (tasks != null) {

			for (Task task : tasks) {
				task.setAssignedUser(null);
				taskRepo.save(task);
			}

		}
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasRole('USER_PROJECT_OWNER')")
	@DeleteMapping("/delete")
	@Transactional
	public ResponseEntity<Void> deleteProject() {

		Project project = ProjectUtil.getProjectNotNull();

		List<User> projectUsers = userRepo.findAllByProject(project).orElse(null);

		if (projectUsers != null) {

			for (User projectUser : projectUsers) {
				projectUser.setRole(Role.ROLE_USER);
			}
			
		}
		
		SecurityContextUtil.getUser().setRole(Role.ROLE_USER);
		projectRepo.delete(project);

		return ResponseEntity.status(204).build();

	}

}
