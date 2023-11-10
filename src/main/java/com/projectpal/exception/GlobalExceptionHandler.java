package com.projectpal.exception;

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
import com.projectpal.exception.client.BadRequestException;
import com.projectpal.exception.client.ForbiddenException;
import com.projectpal.exception.client.ResourceNotFoundException;
import com.projectpal.exception.server.InternalServerErrorException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice({ "com.projectpal.controller" })
@Slf4j
public class GlobalExceptionHandler {

	// Default Exception Object
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception ex) {

		log.error("Uknown error occured: {}, intervention required", ex.getMessage());

		return ResponseEntity.status(500).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised when null request is received into a @RequestBody annotated method
	// parameter

	@ExceptionHandler( HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(Exception ex) {

		log.debug("@RequestBody body not found Error: {}", ex.getMessage());

		return ResponseEntity.status(400).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised when content type not supported

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(Exception ex) {

		log.debug("Http Content type Error: {}", ex.getMessage());

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

		ValidationExceptionResponse response = new ValidationExceptionResponse(stringErrors);

		log.debug("Validation error: {}", response.getErrors());

		return ResponseEntity.status(400).body(response);
	}

	// Raised by @RequestParam when request parameter is missing or null

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ExceptionResponse> handleException(MissingServletRequestParameterException ex) {

		log.debug("@RequestParam null parameter error: {}", ex.getMessage());

		return ResponseEntity.status(400).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised when @PreAuthorize annotation denies access

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {

		log.debug("@PreAuthorize annotation access denial error: {}", ex.getMessage());

		return ResponseEntity.status(403).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised Explicitly

	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<ExceptionResponse> handleException(InternalServerErrorException ex) {

		log.debug("InternalServerErrorException: {}", ex.getMessage());

		return ResponseEntity.status(500).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised Explicitly

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {

		log.trace("ResourceNotFoundException: {}", ex.getMessage());

		return ResponseEntity.status(404).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised Explicitly

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ExceptionResponse> handleBadRequestException(BadRequestException ex) {
		
		log.trace("BadRequestException: {}", ex.getMessage());
		
		return ResponseEntity.status(400).body(new ExceptionResponse(ex.getMessage()));
	}

	// Raised Explicitly

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException ex) {
		
		log.trace("ForbiddenException: {}", ex.getMessage());
		
		return ResponseEntity.status(403).body(new ExceptionResponse(ex.getMessage()));
	}

}
