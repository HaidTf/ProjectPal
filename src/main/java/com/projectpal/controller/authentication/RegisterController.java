package com.projectpal.controller.authentication;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.authentication.RegisterRequest;
import com.projectpal.dto.response.AuthenticationResponse;
import com.projectpal.dto.response.exception.DataIntegrityExceptionResponse;
import com.projectpal.service.authentication.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth/register")
@RequiredArgsConstructor
@Slf4j
public class RegisterController {

	private final AuthenticationService authService;

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<DataIntegrityExceptionResponse> handleException(DataIntegrityViolationException ex) {
		
		DataIntegrityExceptionResponse response = new DataIntegrityExceptionResponse(ex);
		
		if (response.isConstraintViolation()) {
			
			log.info("API:Registration failed due to constraint violation: {}", response.getConstraintViolated());

			return ResponseEntity.status(409).body(response);
		}
		throw ex;
	}

	@PostMapping("")
	public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {

		log.info("API:POST/api/auth/register invoked with email:{}", request.getEmail());

		AuthenticationResponse response = authService.register(request);

		return ResponseEntity.ok(response);
	}
}
