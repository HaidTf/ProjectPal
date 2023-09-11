package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthenticationRequest {

	@JsonCreator
	public AuthenticationRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	@NotBlank
	@Email
	private final String email;
	@NotBlank
	private final String password;
	
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}

}
