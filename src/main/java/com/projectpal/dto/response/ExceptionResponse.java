package com.projectpal.dto.response;

public class ExceptionResponse {

	public ExceptionResponse(String message) {
		this.message = message;
	}

	private final String message;

	public String getMessage() {
		return message;
	}

}