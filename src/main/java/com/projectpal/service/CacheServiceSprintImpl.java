package com.projectpal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.repository.SprintRepository;

@Service
public class CacheServiceSprintImpl {

	@Autowired
	public CacheServiceSprintImpl(CacheService cacheService, SprintRepository sprintRepo) {
		this.cacheService = cacheService;
		this.sprintRepo = sprintRepo;
	}

	private final CacheService cacheService;

	private final SprintRepository sprintRepo;

	public static final String sprintListCache = "sprintListCache";

	public List<Sprint> getCachedSprintList(Project project) {

		return cacheService.getCachedObjects(sprintListCache, project.getId(), sprintRepo,
				repo -> repo.findAllByProjectId(project.getId()));
	}

	public void deleteSprintFromCacheAndCascadeDeleteChildren(Sprint sprint) {
		
		cacheService.evictListFromCache(sprintListCache, sprint.getProject().getId());
		
		cacheService.evictListFromCache(CacheServiceUserStoryImpl.sprintUserStoryListCache, sprint.getId());

	}
}
