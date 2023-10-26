package com.projectpal.service.userstory;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;

public interface UserStoryService {

	public UserStory findUserStoryById(long userStoryId);

	public UserStory findUserStoryByIdAndEpicProject(long userStoryId, Project project);

	public List<UserStory> findUserStoriesByEpicAndProgressFromDbOrCache(long epicId, Set<Progress> progress,
			Sort sort);

	public List<UserStory> findUserStoriesByEpicAndProgressFromDb(Epic epic, Set<Progress> progress, Sort sort);

	public void createUserStory(long epicId, UserStory userStory);

	public void updateDescription(long userStoryId, String description);

	public void updatePriority(long userStoryId, int priority);

	public void updateProgress(long userStoryId, Progress progress);

	public void deleteUserStory(long userStoryId);

	public void sort(List<UserStory> userStories, Sort sort);

}
