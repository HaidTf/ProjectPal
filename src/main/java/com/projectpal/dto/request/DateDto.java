package com.projectpal.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public final class DateDto {

	@JsonCreator
	public DateDto(LocalDate date) {
		this.date = date;
	}

	@NotNull
	@JsonAlias({ "startDate", "endDate" })
	private final LocalDate date;

}
