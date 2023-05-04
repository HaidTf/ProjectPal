package com.projectpal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.User;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.UserRepository;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	public UserController(UserRepository userRepo, PasswordEncoder encoder) {
		this.encoder = encoder;
		this.userRepo = userRepo;
	}

	private final PasswordEncoder encoder;

	private final UserRepository userRepo;
	
	

	@GetMapping("")
	public ResponseEntity<User> getUser() {
		User user = SecurityContextUtil.getUser();
		user.setPassword(null);
		return ResponseEntity.ok(user);
	}

	@PatchMapping("/email")
	@Transactional
	public ResponseEntity<Void> updateEmail(@RequestBody String email) {
		if (email == null)
			throw new BadRequestException("email is null");
		User user = SecurityContextUtil.getUser();
		user.setEmail(email);
		userRepo.save(user);
		return ResponseEntity.status(204).build();
	}

	@PatchMapping("/password")
	@Transactional
	public ResponseEntity<Void> updatePassword(@RequestBody String password) {
		if (password == null)
			throw new BadRequestException("password is null");
		User user = SecurityContextUtil.getUser();
		user.setPassword(encoder.encode(password));
		userRepo.save(user);
		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/delete")
	@Transactional
	public ResponseEntity<Void> deleteUser() {
		userRepo.delete(SecurityContextUtil.getUser());
		return ResponseEntity.status(204).build();
	}
	
	@PatchMapping("/addtoproject")
	@Transactional
	public ResponseEntity<Void> addUserToProject(@RequestParam String name){
		
		ProjectUtil.onlyProjectOwnerAllowed();
		
		if (name == null)
			throw new BadRequestException("the name you typed is null");
		
		User toAddUser = userRepo.findUserByName(name).orElseThrow(()-> new ResourceNotFoundException("the User you requested is not found"));
		
		if (toAddUser.getProject() != null)
			throw new ConflictException("User is already in a project");
		
		toAddUser.setProject(ProjectUtil.getProjectNotNull());
		
		userRepo.save(toAddUser);
		
		return ResponseEntity.status(204).build();
		
		
	}

}
