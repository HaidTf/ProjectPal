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

	@NotBlank
	@Size(min = 3, max = 60)
	private final String name;

	@Nullable
	@Size(max = 300)
	private final String description;

}
