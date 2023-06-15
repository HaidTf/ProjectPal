package com.projectpal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;

@Service
public class CacheServiceProjectAddOn {

	@Autowired
	public CacheServiceProjectAddOn(CacheService cacheService, CacheServiceEpicImpl cacheServiceEpicImpl,
			CacheServiceSprintImpl cacheServiceSprintImpl) {
		this.cacheService = cacheService;
		this.cacheServiceEpicImpl = cacheServiceEpicImpl;
		this.cacheServiceSprintImpl = cacheServiceSprintImpl;
	}
	
	private final CacheService cacheService;
	
	private final CacheServiceEpicImpl cacheServiceEpicImpl;
	
	private final CacheServiceSprintImpl cacheServiceSprintImpl;
	
	// Project Deletion -> Cascade Remove of all child entities from cache:

	public void DeleteEntitiesInCacheOnProjectDeletion(Project project) {

		List<Epic> epics = cacheServiceEpicImpl.getNotDoneEpicListFromCacheOrDatabase(project);
		List<Sprint> sprints = cacheServiceSprintImpl.getNotDoneSprintListFromCacheOrDatabase(project);

		for (Epic epic : epics)
			cacheService.evictListFromCache(CacheServiceUserStoryImpl.epicUserStoryListCache, epic.getId());

		for (Sprint sprint : sprints)
			cacheService.evictListFromCache(CacheServiceUserStoryImpl.sprintUserStoryListCache, sprint.getId());

		cacheService.evictListFromCache(CacheServiceEpicImpl.epicListCache, project.getId());
		cacheService.evictListFromCache(CacheServiceSprintImpl.sprintListCache, project.getId());
	}
	
}
