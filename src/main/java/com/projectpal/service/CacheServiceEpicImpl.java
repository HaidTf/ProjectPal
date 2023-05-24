package com.projectpal.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.UserStory;
import com.projectpal.repository.EpicRepository;

@Service
public class CacheServiceEpicImpl {

	@Autowired
	public CacheServiceEpicImpl(CacheService cacheService,EpicRepository epicRepo,CacheServiceUserStoryImpl cacheServiceUserStoryImpl) {
		this.cacheService = cacheService;
		this.epicRepo = epicRepo;
		this.cacheServiceUserStoryImpl = cacheServiceUserStoryImpl;
	}

	private final CacheService cacheService;

	private final EpicRepository epicRepo;

	private final CacheServiceUserStoryImpl cacheServiceUserStoryImpl;
	
	public static final String epicListCache = "epicListCache";

	public List<Epic> getCachedEpicList(Project project) {

		return cacheService.getCachedObjects(epicListCache, project.getId(), epicRepo,
				repo -> repo.findAllByProjectId(project.getId()));
	}

	public void updateEpicProperty(Epic epic, Function<Epic, Void> updateEpicProperty) {

		cacheService.updateObjectPropertyInCache(epicListCache, epic.getProject().getId(), epic,
				Epic::getId, updateEpicProperty);
	}

	public void DeleteEpicFromCacheAndCascadeDeleteChildren(Epic epic) {

		cacheService.deleteObjectFromCache(epicListCache, epic.getProject().getId(), epic, Epic::getId);

		// Removal of UserStory entities from all cache due to cascadeRemove of
		// userStories
		// from database when epic parent is deleted

		List<UserStory> userStories = cacheServiceUserStoryImpl.getCachedEpicUserStoryList(epic);

		for (UserStory userStory : userStories)
			cacheService.deleteObjectFromCache(CacheServiceUserStoryImpl.sprintUserStoryListCache, userStory.getSprint().getId(),
					userStory, UserStory::getId);

		cacheService.evictListFromCache(CacheServiceUserStoryImpl.epicUserStoryListCache, epic.getId());
	}

}
