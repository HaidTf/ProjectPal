package com.projectpal.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;

@ControllerAdvice("com.projectpal.controller")
public class ExceptionHandlerController {

	@ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex){
		return ResponseEntity.status(404).body(ex.getMessage());
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<String> handleBadRequestException(BadRequestException ex){
		return ResponseEntity.status(400).body(ex.getMessage());
	}
	
	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<String> handleForbiddenException(ForbiddenException ex) {
		return ResponseEntity.status(403).body(ex.getMessage());
	}
	

	
}
