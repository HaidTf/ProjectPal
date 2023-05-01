package com.projectpal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestJwtController {
	
	@GetMapping
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("hello from test control");
	}

}