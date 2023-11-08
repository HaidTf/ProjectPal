package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.User;
import com.projectpal.service.admin.user.AdminUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users/{userId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

	private final AdminUserService userService;

	@GetMapping()
	public ResponseEntity<User> getUser(@PathVariable long userId) {

		return ResponseEntity.ok(userService.findUserById(userId));

	}

	@DeleteMapping()
	public ResponseEntity<Void> deleteUser(@PathVariable long userId) {

		userService.deleteUser(userId);

		return ResponseEntity.status(204).build();
	}

}
