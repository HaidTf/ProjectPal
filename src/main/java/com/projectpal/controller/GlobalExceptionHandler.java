package com.projectpal.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;

import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;

@ControllerAdvice("com.projectpal.controller")
public class GlobalExceptionHandler {
	
	//Default Exception Object
	@ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
		ex.printStackTrace(System.out);
        return ResponseEntity.status(500).body(ex.getMessage());
    }
	
	//Raised when null request is received into a @RequestBody annotated method parameter
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
		return ResponseEntity.status(400).body(ex.getMessage());
	}
	
	//Raised when a bean validation error occurs due to @Valid annotation
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException ex) {
		
        BindingResult result = ex.getBindingResult();
        
        List<String> errors = result.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        
        return ResponseEntity.status(400).body(errors);
    }
	
	//Raised when @PreAuthorize annotation denies access
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex){
		return ResponseEntity.status(403).body(ex.getMessage());
	}
	
	//Raised Explicitly
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex){
		return ResponseEntity.status(404).body(ex.getMessage());
	}
	
	//Raised Explicitly
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<String> handleBadRequestException(BadRequestException ex){
		return ResponseEntity.status(400).body(ex.getMessage());
	}
	
	//Raised Explicitly
	
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<String> handleForbiddenException(ForbiddenException ex) {
		return ResponseEntity.status(403).body(ex.getMessage());
	}
	

	
}
