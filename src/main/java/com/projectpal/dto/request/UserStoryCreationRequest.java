package com.projectpal.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.projectpal.entity.UserStory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public final class UserStoryCreationRequest {

	@JsonCreator
	public UserStoryCreationRequest(UserStory userStory, Long epicId) {
		this.userStory = userStory;
		this.epicId = epicId;
	}

	@Valid
	private final UserStory userStory;

	@NotNull
	private final Long epicId;

	public UserStory getUserStory() {
		return userStory;
	}

	public Long getEpicId() {
		return epicId;
	}

}
