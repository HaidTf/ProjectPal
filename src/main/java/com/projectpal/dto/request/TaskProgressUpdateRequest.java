package com.projectpal.dto.request;



import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projectpal.entity.enums.Progress;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class TaskProgressUpdateRequest {

	@JsonCreator
	public TaskProgressUpdateRequest(Progress progress, String report) {
		this.progress = progress;
		this.report = report;
	}

	@NotNull
	private final Progress progress;

	@Nullable
	@JsonProperty("report")
	@Size(max=500)
	private final String report;

	public Progress getProgress() {
		return progress;
	}

	public String getReport() {
		return report;
	}

}
