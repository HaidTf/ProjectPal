package com.projectpal.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.IdDto;
import com.projectpal.dto.response.CustomPageResponse;
import com.projectpal.entity.User;
import com.projectpal.exception.client.BadRequestException;
import com.projectpal.service.admin.user.SuperAdminUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/super")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminController {

	private final SuperAdminUserService userService;

	@GetMapping("/admins")
	public ResponseEntity<CustomPageResponse<User>> getAllAdmins(
			@PageableDefault(page = 0, size = 20, sort = "name", direction = Direction.DESC) Pageable pageable) {

		Page<User> admins = userService.findAllAdmins(pageable);

		return ResponseEntity.ok(new CustomPageResponse<User>(admins));
	}

	@PostMapping("/admins")
	public ResponseEntity<Void> promoteUserToAdmin(@Valid IdDto userIdHolder,
			@AuthenticationPrincipal User superAdmin) {

		if (superAdmin.getId() == userIdHolder.getId())
			throw new BadRequestException("Super admin is not allowed to be modified");

		userService.promoteUserToAdmin(userIdHolder.getId());

		return ResponseEntity.status(204).build();

	}

	@DeleteMapping("/admins/{adminId}")
	public ResponseEntity<Void> demoteAdmin(@PathVariable Long adminId) {

		userService.demoteAdmin(adminId);

		return ResponseEntity.status(204).build();
	}

}
