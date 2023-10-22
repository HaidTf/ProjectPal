package com.projectpal.dto.response.entity.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserIdAndNameDto {

	private final long id;
	
	private final String name;
}
