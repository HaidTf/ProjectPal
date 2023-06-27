package com.projectpal.dto.response;

import java.util.List;

public final class ValidationExceptionResponse {

	public ValidationExceptionResponse(List<String> errors) {
		this.errors = errors;
	}

	private final List<String> errors;

	public List<String> getErrors() {
		return errors;
	}
}
