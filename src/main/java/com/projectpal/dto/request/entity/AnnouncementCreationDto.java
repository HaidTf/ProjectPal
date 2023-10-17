package com.projectpal.dto.request.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnnouncementCreationDto {

	@NotBlank
	@Size(min = 3, max = 100)
	private final String title;

	@Size(max = 300)
	private final String description;

}
