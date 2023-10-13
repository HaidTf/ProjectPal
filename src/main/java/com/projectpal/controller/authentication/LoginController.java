package com.projectpal.controller.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.AuthenticationRequest;
import com.projectpal.dto.response.AuthenticationResponse;
import com.projectpal.dto.response.ExceptionResponse;
import com.projectpal.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class LoginController {

	private final AuthenticationService authService;

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ExceptionResponse> handleException(AuthenticationException ex) {
		if (ex instanceof BadCredentialsException) {
			return ResponseEntity.status(401).header("WWW-Authenticate", "Basic realm=\"User Login\"")
					.body(new ExceptionResponse("Login failed. Incorrect email or password"));
		} else {
			return ResponseEntity.status(500).body(new ExceptionResponse("Login failed. Unknown error occured"));
		}
	}

	@PostMapping("")
	public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {

		return ResponseEntity.ok(authService.authenticate(request));
	}
}
