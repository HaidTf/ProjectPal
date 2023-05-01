package com.projectpal.exception;

public class ConflictException extends RuntimeException {
	private static final long serialVersionUID = 5L;

	public ConflictException(String message) {
		super(message);
	}
}
