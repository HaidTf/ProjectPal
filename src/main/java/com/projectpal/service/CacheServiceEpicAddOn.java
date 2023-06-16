package com.projectpal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.repository.EpicRepository;

@Service
public class CacheServiceEpicAddOn {

	@Autowired
	public CacheServiceEpicAddOn(CacheService cacheService,EpicRepository epicRepo,CacheServiceUserStoryAddOn cacheServiceUserStoryImpl) {
		this.cacheService = cacheService;
		this.epicRepo = epicRepo;
		this.cacheServiceUserStoryImpl = cacheServiceUserStoryImpl;
	}

	private final CacheService cacheService;

	private final EpicRepository epicRepo;

	private final CacheServiceUserStoryAddOn cacheServiceUserStoryImpl;
	
	public static final String epicListCache = "epicListCache";

	public List<Epic> getNotDoneEpicListFromCacheOrDatabase(Project project) {

		return cacheService.getObjectsFromCacheOrDatabase(epicListCache, project.getId(), epicRepo,
				repo -> repo.findAllByProjectIdAndProgressNot(project.getId(),Progress.DONE));
	}

	public void deleteEpicFromCacheAndCascadeDeleteChildren(Epic epic) {

		cacheService.evictListFromCache(epicListCache, epic.getProject().getId());

		// Removal of UserStory entities from all cache due to cascadeRemove of
		// userStories
		// from database when epic parent is deleted

		List<UserStory> userStories = cacheServiceUserStoryImpl.getEpicUserStoryListFromCacheOrDatabase(epic);

		for (UserStory userStory : userStories)
			cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache, userStory.getSprint().getId());
					

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, epic.getId());
	}

}
