package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public final class IdDto {

	@JsonCreator
	public IdDto(long id) {
		this.id = id;
	}

	@NotNull
	@JsonAlias({ "userId","userStoryId" })
	private final long id;

}
