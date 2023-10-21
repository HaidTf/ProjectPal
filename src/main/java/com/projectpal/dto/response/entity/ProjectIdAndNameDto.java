package com.projectpal.dto.response.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class ProjectIdAndNameDto {

	private final long id;

	private final String name;

}
