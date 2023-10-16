package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnnouncementResponseDto {

	private final long id;

	private final String title;

	private final String description;

	private final LocalDate issueDate;
	
}
