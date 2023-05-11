package com.projectpal.controller;

import java.util.List;

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

import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.utils.SecurityContextUtil;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	public UserController(UserRepository userRepo, PasswordEncoder encoder, TaskRepository taskRepo) {
		this.encoder = encoder;
		this.userRepo = userRepo;
		this.taskRepo = taskRepo;
	}

	private final PasswordEncoder encoder;

	private final UserRepository userRepo;

	private final TaskRepository taskRepo;

	@GetMapping("")
	public ResponseEntity<User> getUser() {
		User user = SecurityContextUtil.getUser();
		user.setPassword(null);
		return ResponseEntity.ok(user);
	}

	@PreAuthorize("!(hasRole('ADMIN'))")
	@PatchMapping("/update/email")
	@Transactional
	public ResponseEntity<Void> updateEmail(@RequestBody String email) {
		if (email == null)
			throw new BadRequestException("email is null");
		User user = SecurityContextUtil.getUser();
		user.setEmail(email);
		userRepo.save(user);
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("!(hasRole('ADMIN'))")
	@PatchMapping("/update/password")
	@Transactional
	public ResponseEntity<Void> updatePassword(@RequestBody String password) {
		if (password == null)
			throw new BadRequestException("password is null");
		User user = SecurityContextUtil.getUser();
		user.setPassword(encoder.encode(password));
		userRepo.save(user);
		return ResponseEntity.status(204).build();
	}

	@PatchMapping("/update/project/exit")
	@Transactional
	public ResponseEntity<Void> exitProject() {

		User user = SecurityContextUtil.getUser();

		if (user.getProject() != null) {
			user.setProject(null);
			user.setRole(Role.ROLE_USER);
			userRepo.save(user);
		}

		List<Task> tasks = taskRepo.findAllByAssignedUser(user).orElse(null);

		for (Task task : tasks) {
			if (task.getAssignedUser() != null) {
				task.setAssignedUser(null);
				taskRepo.save(task);
			}
		}

		return ResponseEntity.status(204).build();

	}

}
