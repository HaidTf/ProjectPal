package com.projectpal.dto.request;

import com.projectpal.entity.UserStory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class UserStoryCreationRequest {

	public UserStoryCreationRequest(UserStory userStory, Long epicId) {
		this.userStory = userStory;
		this.epicId = epicId;
	}

	@Valid
	private UserStory userStory;
	
	@NotNull
	private Long epicId;

	public UserStory getUserStory() {
		return userStory;
	}

	public void setUserStory(UserStory userStory) {
		this.userStory = userStory;
	}

	public Long getEpicId() {
		return epicId;
	}

	public void setEpicId(Long epicId) {
		this.epicId = epicId;
	}
	
}
