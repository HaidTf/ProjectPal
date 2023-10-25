package com.projectpal.dto.request.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.dto.request.NameAndDescriptionDto;
import com.projectpal.dto.request.PriorityDto;

import jakarta.validation.Valid;
import lombok.Getter;

@Getter
public class TaskCreationDto {

	@JsonCreator
	public TaskCreationDto(String name, String description, int priority) {
		this.nameAndDescriptionAttribute = new NameAndDescriptionDto(name, description);
		this.priorityAttribute = new PriorityDto(priority);
	}

	@Valid
	private final NameAndDescriptionDto nameAndDescriptionAttribute;

	@Valid
	private final PriorityDto priorityAttribute;

}
