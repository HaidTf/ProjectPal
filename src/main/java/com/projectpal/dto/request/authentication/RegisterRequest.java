package com.projectpal.dto.request.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public final class RegisterRequest {

	@JsonCreator
	public RegisterRequest(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@NotBlank(message = "name must not be blank")
	@Size(min = 3, max = 20, message = "name must be within the 3-20 character range")
	private final String name;

	@NotBlank(message = "email must not be blank")
	@Email(message = "email is not in the correct format")
	@Size(min = 3, max = 320, message = "email must be within the 3-320 character range")
	private final String email;

	@NotBlank(message = "password must not be blank")
	@Size(min = 6, max = 127, message = "password must be within the 6-127 character range")
	@Pattern(regexp = "^[^\\s]*$", message = "password must not contain spaces")
	private final String password;

}
