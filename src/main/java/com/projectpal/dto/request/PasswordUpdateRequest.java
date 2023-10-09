package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class PasswordUpdateRequest {

	@JsonCreator
	public PasswordUpdateRequest(String password) {
		this.password = password;
	}

	@NotBlank
	@Size(min = 6, max = 20)
	private final String password;

	public String getPassword() {
		return password;
	}

}