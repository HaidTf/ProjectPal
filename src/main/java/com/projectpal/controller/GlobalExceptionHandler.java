package com.projectpal.controller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.BindException;

import com.projectpal.dto.response.exception.ExceptionResponse;
import com.projectpal.dto.response.exception.ValidationExceptionResponse;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;

@ControllerAdvice({"com.projectpal.controller","com.projectpal.service"})
public class GlobalExceptionHandler {

	// Default Exception Object
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
		ex.printStackTrace(System.out);
		return ResponseEntity.status(500).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised when null request is received into a @RequestBody annotated method
	// parameter

	@ExceptionHandler({HttpMessageNotReadableException.class,HttpMediaTypeNotSupportedException.class})
	public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(Exception ex) {
		return ResponseEntity.status(400).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised when a bean validation error occurs due to @Valid annotation

	@ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
	public ResponseEntity<ValidationExceptionResponse> handleValidationException(BindException ex) {

		BindingResult result = ex.getBindingResult();

		List<ObjectError> errors = result.getAllErrors();

		List<String> stringErrors = new ArrayList<>(errors.size());

		for (ObjectError err : errors)
			stringErrors.add(err.getCodes()[0] + " " + err.getDefaultMessage());

		return ResponseEntity.status(400).body(new ValidationExceptionResponse(stringErrors));
	}

	//Raised by @RequestParam when request parameter is missing or null
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ExceptionResponse> handleException(MissingServletRequestParameterException ex) {
		return ResponseEntity.status(400).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised when @PreAuthorize annotation denies access

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
		return ResponseEntity.status(403).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised Explicitly

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return ResponseEntity.status(404).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised Explicitly

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException ex) {
		return ResponseEntity.status(400).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised Explicitly

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException ex) {
		return ResponseEntity.status(403).body(new ExceptionResponse(ex.getMessage()));
	}

}
