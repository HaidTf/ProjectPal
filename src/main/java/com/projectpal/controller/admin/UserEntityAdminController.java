package com.projectpal.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.UserRepository;
import com.projectpal.utils.SecurityContextUtil;

@RestController
@RequestMapping("/admin/user")
public class UserEntityAdminController {

	@Autowired
	public UserEntityAdminController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	private final PasswordEncoder passwordEncoder;

	private final UserRepository userRepo;

	@GetMapping("/get/{id}")
	public ResponseEntity<User> getUser(@PathVariable long id) {

		return ResponseEntity
				.ok(userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found")));
	}

	@GetMapping("/get/{name}")
	public ResponseEntity<User> getUserByName(@PathVariable String name) {

		return ResponseEntity
				.ok(userRepo.findUserByName(name).orElseThrow(() -> new ResourceNotFoundException("user not found")));
	}

	@PatchMapping("/update/email/{id}")
	@Transactional
	public ResponseEntity<Void> updateEmail(@PathVariable long id, @RequestBody String email) {

		if (email == null)
			throw new BadRequestException("email is null");

		userRepo.updateEmailById(id, email);

		return ResponseEntity.status(204).build();
	}

	@PatchMapping("/update/password/{id}")
	@Transactional
	public ResponseEntity<Void> updatePassword(@PathVariable long id, @RequestBody String password) {

		if (password == null)
			throw new BadRequestException("password is null");

		userRepo.updatePasswordById(id, passwordEncoder.encode(password));

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteUserById(@PathVariable long id) {

		if (SecurityContextUtil.getUser().getId() == id)
			throw new BadRequestException("you cant delete yourself");

		User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found"));

		if (SecurityContextUtil.getUser().getRole() == Role.ROLE_ADMIN) {

			if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_SUPER_ADMIN)
				throw new ForbiddenException("you cant delete a fellow admin");

		}
		userRepo.deleteById(id);

		return ResponseEntity.status(204).build();

	}

}
