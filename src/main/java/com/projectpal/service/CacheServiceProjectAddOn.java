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
	public CacheServiceProjectAddOn(CacheService cacheService, CacheServiceEpicAddOn cacheServiceEpicAddOn,
			CacheServiceSprintAddOn cacheServiceSprintAddOn) {
		this.cacheService = cacheService;
		this.cacheServiceEpicAddOn = cacheServiceEpicAddOn;
		this.cacheServiceSprintAddOn = cacheServiceSprintAddOn;
	}
	
	private final CacheService cacheService;
	
	private final CacheServiceEpicAddOn cacheServiceEpicAddOn;
	
	private final CacheServiceSprintAddOn cacheServiceSprintAddOn;
	
	// Project Deletion -> Cascade Remove of all child entities from cache:

	public void DeleteEntitiesInCacheOnProjectDeletion(Project project) {

		List<Epic> epics = cacheServiceEpicAddOn.getNotDoneEpicListFromCacheOrDatabase(project);
		List<Sprint> sprints = cacheServiceSprintAddOn.getNotDoneSprintListFromCacheOrDatabase(project);

		for (Epic epic : epics)
			cacheService.evictListFromCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, epic.getId());

		for (Sprint sprint : sprints)
			cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache, sprint.getId());

		cacheService.evictListFromCache(CacheServiceEpicAddOn.epicListCache, project.getId());
		cacheService.evictListFromCache(CacheServiceSprintAddOn.sprintListCache, project.getId());
	}
	
}
