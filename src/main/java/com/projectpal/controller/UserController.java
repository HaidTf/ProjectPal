package com.projectpal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.User;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.SecurityContextService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	public UserController(SecurityContextService service, UserRepository userRepo,PasswordEncoder encoder) {
		this.service = service;
		this.encoder = encoder;
		this.userRepo = userRepo;
	}

	private final PasswordEncoder encoder;
	
	private final SecurityContextService service;
	
	private final UserRepository userRepo;
	
	@GetMapping("")
	public ResponseEntity<User> getUser(){
		return ResponseEntity.ok(service.getUser());
	}
	
	@PutMapping("/email")
	public ResponseEntity<String> updateEmail(String email){
		User user = service.getUser();
		user.setEmail(email);
		userRepo.save(user);
		return ResponseEntity.ok(email);
	}
	@PutMapping("/password")
	public ResponseEntity<String> updatePassword(String password){
		User user = service.getUser();
		user.setPassword(encoder.encode(password));
		userRepo.save(user);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Boolean> deleteUser(){
		userRepo.delete(service.getUser());
		return ResponseEntity.ok(true);
	}
	
}
