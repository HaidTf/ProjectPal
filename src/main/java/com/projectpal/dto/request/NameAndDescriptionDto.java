package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class NameAndDescriptionDto {

	@JsonCreator
	public NameAndDescriptionDto(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@NotBlank(message = "name must not be blank")
	@Size(min = 3, max = 60, message = "name must be within the 3-60 character range")
	private final String name;

	@Nullable
	@Size(max = 300, message = "description must be less than 300 character")
	private final String description;

}
