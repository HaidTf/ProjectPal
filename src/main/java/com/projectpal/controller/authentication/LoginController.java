package com.projectpal.controller.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.authentication.AuthenticationRequest;
import com.projectpal.dto.response.AuthenticationResponse;
import com.projectpal.dto.response.exception.ExceptionResponse;
import com.projectpal.service.authentication.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

	private final AuthenticationService authService;

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ExceptionResponse> handleException(AuthenticationException ex) {

		if (ex instanceof BadCredentialsException) {

			log.info("API:Authentication failed. Incorrect email or password ");

			return ResponseEntity.status(401).header("WWW-Authenticate", "Basic realm=\"User Login\"")
					.body(new ExceptionResponse("Login failed. Incorrect email or password"));
		} else {
			throw ex;
		}
	}

	@PostMapping("")
	public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {

		log.info("API:POST/api/auth/login invoked with email:{}", request.getEmail());

		return ResponseEntity.ok(authService.authenticate(request));
	}
}
