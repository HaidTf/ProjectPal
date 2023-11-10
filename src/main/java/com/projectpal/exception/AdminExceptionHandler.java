package com.projectpal.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.projectpal.dto.response.exception.ExceptionResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice("com.projectpal.controller.admin")
@Slf4j
public class AdminExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
		
		log.info("Unauthorized access attempt to ADMIN endpoints");
		
		return ResponseEntity.status(404).body(new ExceptionResponse("Endpoint not found"));
	}

}
