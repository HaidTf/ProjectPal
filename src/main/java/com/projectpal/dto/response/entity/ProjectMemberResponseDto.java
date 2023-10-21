package com.projectpal.dto.response.entity;

import com.projectpal.entity.enums.Role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectMemberResponseDto {

	private final long id;
	
	private final String name;
	
	private final Role role;
}
