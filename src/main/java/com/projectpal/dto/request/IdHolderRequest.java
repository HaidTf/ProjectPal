package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotNull;

public final class IdHolderRequest {

	@JsonCreator
	public IdHolderRequest(long id) {
		this.id = id;
	}

	@NotNull
	@JsonAlias({ "userId","userStoryId" })
	private final long id;

	public long getId() {
		return id;
	}

}
