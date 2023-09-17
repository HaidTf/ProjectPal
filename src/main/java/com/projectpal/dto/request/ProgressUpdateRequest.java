package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.entity.enums.Progress;

import jakarta.validation.constraints.NotNull;

public final class ProgressUpdateRequest {

	@JsonCreator
	public ProgressUpdateRequest(Progress progress) {
		this.progress = progress;
	}

	@NotNull
	private final Progress progress;

	public Progress getProgress() {
		return progress;
	}
	
}
