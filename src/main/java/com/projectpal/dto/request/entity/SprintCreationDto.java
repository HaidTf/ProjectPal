package com.projectpal.dto.request.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.projectpal.dto.request.NameAndDescriptionDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SprintCreationDto {

	@JsonUnwrapped
	@Valid
	private final NameAndDescriptionDto nameAndDescriptionAttribute;

	@NotNull
	private final LocalDate startDate;

	@NotNull
	private final LocalDate endDate;

}
