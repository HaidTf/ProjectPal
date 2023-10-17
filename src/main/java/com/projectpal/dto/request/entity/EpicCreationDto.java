package com.projectpal.dto.request.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.projectpal.dto.request.NameAndDescriptionDto;
import com.projectpal.dto.request.PriorityDto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EpicCreationDto {
	
	@JsonUnwrapped
	@Valid
	private final NameAndDescriptionDto nameAndDescriptionAttribute;

	@JsonUnwrapped
	@Valid
	private final PriorityDto priorityAttribute;

}
