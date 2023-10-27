package com.projectpal.exception.server;

public class InternalServerErrorException extends RuntimeException {
	private static final long serialVersionUID = 4L;

	public InternalServerErrorException(String message) {
		super(message);
	}
}
