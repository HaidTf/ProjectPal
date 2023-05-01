package com.projectpal.controller.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.controller.requestobj.AuthenticationRequest;
import com.projectpal.controller.responseobj.AuthenticationResponse;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.InternalServerErrorException;
import com.projectpal.service.AuthenticationService;

@RestController
@RequestMapping("/auth/login")
public class LoginController {
	
	@Autowired
	public LoginController(AuthenticationService authService) {
		this.authService = authService;
	}
	private final AuthenticationService authService;
	
	
	@ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleException(AuthenticationException ex) {
		if (ex instanceof BadCredentialsException) {
			throw new BadRequestException("incorrect email or password");
		}
		else {
			throw new InternalServerErrorException("Unknown error occured");
		}
    }
	
	
	@PostMapping("")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		
		if(request.getEmail() == null || request.getPassword() == null) {
			
			throw new BadRequestException("null value for email or password");
		}
		return ResponseEntity.ok(authService.authenticate(request));
	}
}
