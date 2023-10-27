package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public final class PasswordDto {

	@JsonCreator
	public PasswordDto(String password) {
		this.password = password;
	}

	@NotBlank(message = "password must not be blank")
	@Size(min = 6, max = 127, message = "password must be within the 6-320 character range")
	@Pattern(regexp = "^[^\\s]*$", message = "password must not contain spaces")
	private final String password;

}
