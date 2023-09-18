package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class RegisterRequest {

	@JsonCreator
	public RegisterRequest(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@NotBlank
	@Size(min=3,max=20)
	private final String name;
	
	@NotBlank
	@Email
	@Size(min=3,max=320)
	private final String email;
	
	@NotBlank
	@Size(min=6,max=127)
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
