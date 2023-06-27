package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public final class RegisterRequest {

	@JsonCreator
	public RegisterRequest(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@NotNull
	private final String name;
	@NotNull
	@Email
	private final String email;
	@NotNull
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
