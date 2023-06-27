package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public final class AuthenticationRequest {

	@JsonCreator
	public AuthenticationRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	@NotNull
	@Email
	private final String email;
	@NotNull
	private final String password;
	
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}

}
