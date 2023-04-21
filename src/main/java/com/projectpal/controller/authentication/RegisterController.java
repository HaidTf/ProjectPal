package com.projectpal.controller.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.controller.requestobj.RegisterRequest;
import com.projectpal.controller.responseobj.AuthenticationResponse;
import com.projectpal.controller.responseobj.exception.DataIntegrityExceptionResponse;
import com.projectpal.service.AuthenticationService;

@RestController
@RequestMapping("/auth/register")
public class RegisterController {

	@Autowired
	public RegisterController(AuthenticationService authService) {
		this.authService = authService;
	}

	private final AuthenticationService authService;

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<DataIntegrityExceptionResponse> handleException(DataIntegrityViolationException ex) {
		DataIntegrityExceptionResponse response = new DataIntegrityExceptionResponse(ex);
		return ResponseEntity.status(response.isConstraintViolation() ? 409 : 500).body(response);
	}

	@PostMapping("")
	@Transactional
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {

		if (request == null || request.getName() == null || request.getEmail() == null
				|| request.getPassword() == null) {
			return ResponseEntity.badRequest().body(null);
		}

		AuthenticationResponse response = authService.register(request);

		return ResponseEntity.ok(response);
	}
}
