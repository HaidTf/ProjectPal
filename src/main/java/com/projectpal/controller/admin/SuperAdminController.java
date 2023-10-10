package com.projectpal.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.repository.UserRepository;

@RestController
@RequestMapping("/admin/super")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

	@Autowired
	public SuperAdminController(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	private final UserRepository userRepo;

	@GetMapping("/list/admin")
	public ResponseEntity<List<User>> getListOfAdmins(){
		return ResponseEntity.ok(userRepo.findAllByRole(Role.ROLE_ADMIN).orElse(null));
	}
	
	@PatchMapping("/update/promotetoadmin")
	@Transactional
	public ResponseEntity<Void> promoteToAdmin(@RequestBody Long id) {
		
		if (SecurityContextUtil.getUser().getId() == id)
			throw new BadRequestException("you cant promote yourself");
		
		userRepo.updateRoleById(id,Role.ROLE_ADMIN);
		
		return ResponseEntity.status(204).build();
	}

	@PatchMapping("/update/demotetouser")
	@Transactional
	public ResponseEntity<Void> demoteToUser(@RequestBody Long id) {
		
		if (SecurityContextUtil.getUser().getId() == id)
			throw new BadRequestException("you cant demote yourself");
		
		userRepo.updateRoleById(id,Role.ROLE_USER);
		
		return ResponseEntity.status(204).build();
	}
}
