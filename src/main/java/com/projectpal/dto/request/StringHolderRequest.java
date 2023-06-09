package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotBlank;

public class StringHolderRequest {

	public StringHolderRequest(String string) {
		this.string = string;
	}

	@NotBlank
	@JsonAlias({ "email", "password", "description", "title", "name" })
	private final String string;

	public String getString() {
		return string;
	}

}
