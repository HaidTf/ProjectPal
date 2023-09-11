package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class RegisterRequest {

	@JsonCreator
	public RegisterRequest(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@NotBlank
	private final String name;
	@NotBlank
	@Email
	private final String email;
	@NotBlank
	private final String password;

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

}
