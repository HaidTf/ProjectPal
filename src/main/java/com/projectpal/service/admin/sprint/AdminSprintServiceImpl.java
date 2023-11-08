package com.projectpal.service.admin.sprint;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Sprint;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.SprintRepository;
import com.projectpal.service.cache.CacheConstants;
import com.projectpal.service.cache.CacheService;
import com.projectpal.service.cache.UserStoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminSprintServiceImpl implements AdminSprintService {

	private final SprintRepository sprintRepo;

	@Qualifier("sprintCacheService")
	private final CacheService<Sprint> sprintCacheService;

	private final UserStoryCacheService userStoryCacheService;

	@Override
	@Transactional(readOnly = true)
	public Sprint findSprintById(long sprintId) {
		return sprintRepo.findById(sprintId).orElseThrow(() -> new EntityNotFoundException(Sprint.class));
	}

	@Override
	@Transactional
	public void deleteSprint(long sprintId) {

		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new EntityNotFoundException(Sprint.class));

		sprintCacheService.evictCache(CacheConstants.SPRINT_CACHE, sprint.getProject().getId());

		userStoryCacheService.evictCache(CacheConstants.SPRINT_USERSTORY_CACHE, sprint.getId());

		sprintRepo.delete(sprint);
	}
}
