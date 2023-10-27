package com.projectpal.dto.request.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public final class AuthenticationRequest {

	@JsonCreator
	public AuthenticationRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	private final String email;

	private final String password;

}
