package com.projectpal.dto.request.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.dto.request.NameAndDescriptionDto;

import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class ProjectCreationDto {

	@JsonCreator
	public ProjectCreationDto(String name,String description) {
		this.nameAndDescriptionAttribute = new NameAndDescriptionDto(name,description);
	}
	
	@Valid
	private final NameAndDescriptionDto nameAndDescriptionAttribute;

}
