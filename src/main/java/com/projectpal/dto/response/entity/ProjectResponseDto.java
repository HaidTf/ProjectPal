package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectResponseDto {

	private final long id;
	
	private final String name;
	
	private final String description;
	
	private final String OwnerName;

	private final LocalDate creationDate;
}
