package com.projectpal.dto.request.entity;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.projectpal.dto.request.NameAndDescriptionDto;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectCreationDto {

	@JsonUnwrapped
	@Valid
	private final NameAndDescriptionDto nameAndDescriptionAttribute;

}
