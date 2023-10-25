package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public final class PriorityDto {

	@JsonCreator
	public PriorityDto(int priority) {
		this.priority = priority;
	}

	@Min(1)
	@Max(10)
	private final int priority;

}
