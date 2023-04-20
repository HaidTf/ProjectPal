package com.projectpal.controller.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.security.AuthenticationResponse;
import com.projectpal.security.RegisterRequest;
import com.projectpal.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth/register")
public class RegisterController {
	
	@Autowired
	public RegisterController(AuthenticationService authService) {
		this.authService = authService;
	}
	private final AuthenticationService authService;
	
	
	@PostMapping
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
		
		if(request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
			return ResponseEntity.badRequest().body(new AuthenticationResponse());
		}
		
		return ResponseEntity.ok(authService.register(request));
	}
}
