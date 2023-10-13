package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public final class PriorityUpdateRequest {

	@JsonCreator
	public PriorityUpdateRequest(int priority) {
		this.priority = priority;
	}

	@Min(1)
	@Max(255)
	private final int priority;

}
