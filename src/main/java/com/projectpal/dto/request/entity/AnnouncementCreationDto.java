package com.projectpal.dto.request.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnnouncementCreationDto {

	@NotBlank(message = "title must not be null")
	@Size(min = 3, max = 100, message = "title must be within the 3-100 character range")
	private final String title;

	@Size(max = 300, message = "description must be less than 300 character")
	private final String description;

}
