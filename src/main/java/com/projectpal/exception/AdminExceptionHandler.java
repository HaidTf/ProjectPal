package com.projectpal.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projectpal.dto.response.exception.ExceptionResponse;

@RestControllerAdvice("com.projectpal.controller.admin")
public class AdminExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity.status(404).body(new ExceptionResponse("Endpoint not found"));
	}

}
