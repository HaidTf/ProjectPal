package com.projectpal.service.admin.epic;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Epic;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.service.cache.CacheConstants;
import com.projectpal.service.cache.CacheService;
import com.projectpal.service.cache.UserStoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminEpicServiceImpl implements AdminEpicService {

	private final EpicRepository epicRepo;

	private final UserStoryRepository userStoryRepo;

	@Qualifier("epicCacheService")
	private final CacheService<Epic> epicCacheService;

	private final UserStoryCacheService userStoryCacheService;

	@Override
	@Transactional(readOnly = true)
	public Epic findEpicById(long epicId) {
		return epicRepo.findById(epicId).orElseThrow(() -> new EntityNotFoundException(Epic.class));
	}

	@Override
	@Transactional
	public void deleteEpic(long epicId) {

		Epic epic = epicRepo.findById(epicId).orElseThrow(() -> new EntityNotFoundException(Epic.class));

		epicCacheService.evictCache(CacheConstants.EPIC_CACHE, epic.getProject().getId());

		List<UserStory> userStories = userStoryCacheService
				.getListFromCache(CacheConstants.EPIC_USERSTORY_CACHE, epic.getId()).orElseGet(() -> userStoryRepo
						.findAllByEpicAndProgressIn(epic, Set.of(Progress.TODO, Progress.INPROGRESS)));

		for (UserStory userStory : userStories) {
			if (userStory.getSprint() != null)
				userStoryCacheService.evictCache(CacheConstants.SPRINT_USERSTORY_CACHE, userStory.getSprint().getId());
		}
		userStoryCacheService.evictCache(CacheConstants.EPIC_USERSTORY_CACHE, epic.getId());

		epicRepo.delete(epic);

	}

}
