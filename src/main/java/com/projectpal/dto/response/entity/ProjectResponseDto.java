package com.projectpal.dto.response.entity;

import lombok.Getter;

@Getter
public class ProjectResponseDto {

	public ProjectResponseDto(long id, String name, String description, long ownerId, String ownerName) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.owner = new UserIdAndNameDto(ownerId, ownerName);
	}

	private final long id;

	private final String name;

	private final String description;

	private final UserIdAndNameDto owner;

}
