package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.entity.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public final class RoleUpdateRequest {

	@JsonCreator
	public RoleUpdateRequest(Role role) {
		this.role = role;
	}

	@NotNull
	private final Role role;

}
