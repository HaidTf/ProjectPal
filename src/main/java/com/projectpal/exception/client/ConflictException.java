package com.projectpal.exception.client;

public class ConflictException extends RuntimeException {
	private static final long serialVersionUID = 5L;

	public ConflictException(String message) {
		super(message);
	}
}
