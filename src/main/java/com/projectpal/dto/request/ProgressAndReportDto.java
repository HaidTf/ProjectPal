package com.projectpal.dto.request;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projectpal.entity.enums.Progress;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public final class ProgressAndReportDto {

	@JsonCreator
	public ProgressAndReportDto(Progress progress, String report) {
		this.progress = progress;
		this.report = report;
	}

	@NotNull(message = "progress must not be null")
	private final Progress progress;

	@Nullable
	@JsonProperty("report")
	@Size(max = 500, message = "report must be less than 500 character")
	private final String report;

}
