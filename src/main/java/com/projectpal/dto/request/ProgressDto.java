package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.entity.enums.Progress;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public final class ProgressDto {

	@JsonCreator
	public ProgressDto(Progress progress) {
		this.progress = progress;
	}

	@NotNull
	private final Progress progress;
	
}
