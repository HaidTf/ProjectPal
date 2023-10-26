package com.projectpal.service.userstory;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;

import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;

public interface SprintUserStoryService {

	public List<UserStory> findUserStoriesBySprintAndProgressListFromDbOrCache(long sprintId, Set<Progress> progress,
			Sort sort);

	public List<UserStory> findUserStoriesBySprintAndProgressFromDb(Sprint sprint, Set<Progress> progress, Sort sort);

	public void addUserStoryToSprint(long userStoryId, long sprintId);

	public void removeUserStoryFromSprint(long userStoryId, long sprintId);

}
