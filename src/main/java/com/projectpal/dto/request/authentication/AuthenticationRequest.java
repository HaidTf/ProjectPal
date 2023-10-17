package com.projectpal.dto.request.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
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

}
