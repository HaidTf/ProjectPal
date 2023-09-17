package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.annotation.Nullable;


public final class DescriptionUpdateRequest {

	@JsonCreator
	public DescriptionUpdateRequest(String description) {
		this.description = description;
	}

	@Nullable
	private final String description;

	public String getDescription() {
		return description;
	}
	
}
