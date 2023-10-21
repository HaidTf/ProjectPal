package com.projectpal.dto.response.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class UserIdAndNameDto {

	private final long id;
	
	private final String name;
}
