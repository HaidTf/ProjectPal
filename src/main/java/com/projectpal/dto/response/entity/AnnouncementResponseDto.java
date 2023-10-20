package com.projectpal.dto.response.entity;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class AnnouncementResponseDto {

	public AnnouncementResponseDto(long id, LocalDate issueDate, long announcerId, String announcerName, String title,
			String description) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.issueDate = issueDate;
		this.announcer = new UserIdAndNameDto(announcerId, announcerName);
	}

	private final long id;

	private final String title;

	private final String description;

	private final LocalDate issueDate;

	private final UserIdAndNameDto announcer;

}
