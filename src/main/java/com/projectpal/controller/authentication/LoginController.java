package com.projectpal.controller.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.security.AuthenticationRequest;
import com.projectpal.security.AuthenticationResponse;
import com.projectpal.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth/login")
public class LoginController {
	
	@Autowired
	public LoginController(AuthenticationService authService) {
		this.authService = authService;
	}
	private final AuthenticationService authService;
	
	
	@PostMapping
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		
		if(request.getEmail() == null || request.getPassword() == null) {
			System.out.println(request.getEmail()+request.getPassword());
			return ResponseEntity.badRequest().body(new AuthenticationResponse("Cant authenticate"));
		}
		return ResponseEntity.ok(authService.authenticate(request));
	}
}
