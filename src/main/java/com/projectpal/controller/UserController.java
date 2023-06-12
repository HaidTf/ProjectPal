package com.projectpal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.StringHolderRequest;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.CacheServiceProjectAddOn;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
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
		user.setPassword(null);
		return ResponseEntity.ok(user);
	}

	@PreAuthorize("!(hasRole('SUPER_ADMIN'))")
	@PatchMapping("/update/email")
	public ResponseEntity<Void> updateEmail(@Valid @RequestBody StringHolderRequest emailHolder) {

		User user = SecurityContextUtil.getUser();
		user.setEmail(emailHolder.getString());
		userRepo.save(user);
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("!(hasRole('SUPER_ADMIN'))")
	@PatchMapping("/update/password")
	public ResponseEntity<Void> updatePassword(@Valid @RequestBody StringHolderRequest passwordHolder) {

		User user = SecurityContextUtil.getUser();
		user.setPassword(encoder.encode(passwordHolder.getString()));
		userRepo.save(user);
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR','USER_PROJECT_PARTICIPATOR','ADMIN')")
	@PatchMapping("/update/project/exit")
	@Transactional
	public ResponseEntity<Void> exitProject() {

		User user = SecurityContextUtil.getUser();

		if (user.getRole() == Role.ROLE_USER_PROJECT_OWNER) {

			List<User> projectUsers = userRepo.findAllByProject(user.getProject()).orElse(null);

			if (projectUsers != null) {

				for (User projectUser : projectUsers) {

					if (projectUser.getRole() == Role.ROLE_USER_PROJECT_OPERATOR) {
						projectUser.setRole(Role.ROLE_USER_PROJECT_OWNER);
						userRepo.save(projectUser);
						break;
					}
				}

			} else {
				cacheServiceProjectAddOn.DeleteEntitiesInCacheOnProjectDeletion(user.getProject());
				projectRepo.delete(user.getProject());
			}
		}

		user.setProject(null);
		user.setRole(Role.ROLE_USER);
		userRepo.save(user);

		Optional<List<Task>> tasks = taskRepo.findAllByAssignedUser(user);

		if (tasks.isPresent()) {

			for (Task task : tasks.get()) {

				task.setAssignedUser(null);
				taskRepo.save(task);

			}
		}
		return ResponseEntity.status(204).build();

	}

}
