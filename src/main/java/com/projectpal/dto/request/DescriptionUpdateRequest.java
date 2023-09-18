package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;

public final class DescriptionUpdateRequest {

	@JsonCreator
	public DescriptionUpdateRequest(String description) {
		this.description = description;
	}

	@Nullable
	@Size(max=300)
	private final String description;

	public String getDescription() {
		return description;
	}

}
