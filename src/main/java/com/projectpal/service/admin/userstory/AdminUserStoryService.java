package com.projectpal.service.admin.userstory;

import com.projectpal.entity.UserStory;

public interface AdminUserStoryService {

	UserStory findUserStoryById(long userStoryId);

	void deleteUserStory(long userStoryId);

}
