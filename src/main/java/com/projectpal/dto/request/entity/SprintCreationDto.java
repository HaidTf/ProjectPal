package com.projectpal.dto.request.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.dto.request.NameAndDescriptionDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SprintCreationDto {

	@JsonCreator
	public SprintCreationDto(String name, String description, LocalDate startDate, LocalDate endDate) {
		this.nameAndDescriptionAttribute = new NameAndDescriptionDto(name, description);
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Valid
	private final NameAndDescriptionDto nameAndDescriptionAttribute;

	@NotNull(message = "start date must not be null")
	private final LocalDate startDate;

	@NotNull(message = "end date must not be null")
	private final LocalDate endDate;

}
