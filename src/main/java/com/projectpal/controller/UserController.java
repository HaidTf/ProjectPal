package com.projectpal.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.PasswordUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.CacheServiceProjectAddOn;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/me")
public class UserController {

	@Autowired
	public UserController(UserRepository userRepo, PasswordEncoder encoder, TaskRepository taskRepo,
			ProjectRepository projectRepo, CacheServiceProjectAddOn cacheServiceProjectAddOn) {
		this.encoder = encoder;
		this.userRepo = userRepo;
		this.taskRepo = taskRepo;
		this.projectRepo = projectRepo;
		this.cacheServiceProjectAddOn = cacheServiceProjectAddOn;
	}

	private final PasswordEncoder encoder;

	private final UserRepository userRepo;

	private final TaskRepository taskRepo;

	private final ProjectRepository projectRepo;

	private final CacheServiceProjectAddOn cacheServiceProjectAddOn;

	@GetMapping("")
	public ResponseEntity<User> getUser() {
		User user = SecurityContextUtil.getUser();
		return ResponseEntity.ok(user);
	}

	@PreAuthorize("!(hasRole('SUPER_ADMIN'))")
	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest) {

		User user = SecurityContextUtil.getUser();
		user.setPassword(encoder.encode(passwordUpdateRequest.getPassword()));
		userRepo.save(user);
		return ResponseEntity.status(204).build();
	}

	// Get All tasks
	// TODO: implement filtering
	@GetMapping("/tasks/all")
	public ResponseEntity<ListHolderResponse<Task>> getUserTasksList() {

		User user = SecurityContextUtil.getUser();

		List<Task> tasks = taskRepo.findAllByAssignedUser(user).orElse(new ArrayList<Task>(0));

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(new ListHolderResponse<Task>(tasks));
	}

	// Get NotDone tasks
	// TODO: implement filtering
	@GetMapping("/tasks/notdone")
	public ResponseEntity<ListHolderResponse<Task>> getUserTaskTodoOrInProgressList() {

		User user = SecurityContextUtil.getUser();

		List<Task> tasks = taskRepo.findAllByAssignedUserAndProgressNot(user, Progress.DONE)
				.orElse(new ArrayList<Task>(0));

		tasks.sort((task1, task2) -> Integer.compare(task1.getPriority(), task2.getPriority()));

		return ResponseEntity.ok(new ListHolderResponse<Task>(tasks));
	}
	
	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR')")
	@DeleteMapping("/project/membership")
	@Transactional
	public ResponseEntity<Void> exitProject() {

		User user = SecurityContextUtil.getUser();

		Project project = user.getProject();

		user.setProject(null);
		userRepo.save(user);

		if (user.getRole() == Role.ROLE_USER_PROJECT_OWNER) {

			Optional<List<User>> projectUsers = userRepo.findAllByProject(project);

			if (projectUsers.isPresent() && projectUsers.get().size() > 0) {

				boolean newProjectOwnerIsSet = false;

				for (User projectUser : projectUsers.get()) {

					if (projectUser.getRole() == Role.ROLE_USER_PROJECT_OPERATOR) {

						projectUser.setRole(Role.ROLE_USER_PROJECT_OWNER);
						userRepo.save(projectUser);

						project.setOwner(projectUser);
						projectRepo.save(project);

						newProjectOwnerIsSet = true;
						break;
					}
				}

				if (!newProjectOwnerIsSet) {

					User newProjectOwner = projectUsers.get().get(0);
					newProjectOwner.setRole(Role.ROLE_USER_PROJECT_OWNER);
					userRepo.save(newProjectOwner);

					project.setOwner(newProjectOwner);
					projectRepo.save(project);
				}
			} else {
				cacheServiceProjectAddOn.DeleteEntitiesInCacheOnProjectDeletion(project);
				projectRepo.delete(project);
			}
		}

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

}
