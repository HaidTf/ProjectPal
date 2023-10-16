package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SprintResponseDto {

	private final long id;

	private final String name;

	private final String description;

	private final LocalDate startDate;

	private final LocalDate endDate;

	private final LocalDate creationDate;

}
