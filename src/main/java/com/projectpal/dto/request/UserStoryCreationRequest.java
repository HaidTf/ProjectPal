package com.projectpal.dto.request;

import com.projectpal.entity.UserStory;

public class UserStoryCreationRequest {

	public UserStoryCreationRequest(UserStory userStory, Long epicId) {
		this.userStory = userStory;
		this.epicId = epicId;
	}

	private UserStory userStory;
	
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
