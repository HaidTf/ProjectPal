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

	@Min(value = 1, message = "priority must be greater or equal to 1")
	@Max(value = 10, message = "priority must be less than or equal to 10")
	private final int priority;

}
