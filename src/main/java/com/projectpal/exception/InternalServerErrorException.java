package com.projectpal.exception;

public class InternalServerErrorException extends RuntimeException {
	private static final long serialVersionUID = 4L;

	public InternalServerErrorException(String message) {
		super(message);
	}
}
