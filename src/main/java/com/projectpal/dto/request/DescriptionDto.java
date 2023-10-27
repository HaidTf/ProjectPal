package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public final class DescriptionDto {

	@JsonCreator
	public DescriptionDto(String description) {
		this.description = description;
	}

	@Nullable
	@Size(max = 300, message = "description must be less than 300 character")
	private final String description;

}
