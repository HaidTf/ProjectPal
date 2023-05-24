package com.projectpal.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.repository.UserStoryRepository;

@Service
public class CacheServiceUserStoryImpl {

	@Autowired
	public CacheServiceUserStoryImpl(CacheService cacheService, UserStoryRepository userStoryRepo) {
		this.cacheService = cacheService;
		this.userStoryRepo = userStoryRepo;
	}

	private final CacheService cacheService;

	private final UserStoryRepository userStoryRepo;

	public static final String epicUserStoryListCache = "epicUserStoryListCache";

	public static final String sprintUserStoryListCache = "sprintUserStoryListCache";

	public List<UserStory> getCachedEpicUserStoryList(Epic epic) {

		return cacheService.getCachedObjects(epicUserStoryListCache, epic.getId(), userStoryRepo,
				repo -> repo.findAllByEpicId(epic.getId()));
	}

	public List<UserStory> getCachedSprintUserStoryList(Sprint sprint) {

		return cacheService.getCachedObjects(sprintUserStoryListCache, sprint.getId(), userStoryRepo,
				repo -> repo.findAllBySprintId(sprint.getId()));
	}

	public void updateUserStoryProperty(UserStory userStory, Function<UserStory, Void> updateUserStoryProperty) {

		cacheService.updateObjectPropertyInCache(epicUserStoryListCache, userStory.getEpic().getId(), userStory,
				UserStory::getId, updateUserStoryProperty);

		cacheService.updateObjectPropertyInCache(sprintUserStoryListCache, userStory.getSprint().getId(), userStory,
				UserStory::getId, updateUserStoryProperty);
	}
}
