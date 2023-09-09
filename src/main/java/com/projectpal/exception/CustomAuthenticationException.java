package com.projectpal.exception;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = 6L;

	public CustomAuthenticationException(String msg, Throwable cause) {
		super(msg, cause);
	}
	public CustomAuthenticationException(String msg) {
		super(msg);
	}

}
