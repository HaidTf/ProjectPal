package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public final class PriorityParameterRequest {

	@JsonCreator
	public PriorityParameterRequest(int priority) {
		this.priority = priority;
	}

	@Min(1)
	@Max(255)
	private final int priority;

	public int getPriority() {
		return priority;
	}

}
