package com.projectpal.service.admin.userstory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.UserStory;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.service.cache.UserStoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserStoryServiceImpl implements AdminUserStoryService {

	private final UserStoryRepository userStoryRepo;

	private final UserStoryCacheService userStoryCacheService;

	@Override
	@Transactional(readOnly = true)
	public UserStory findUserStoryById(long userStoryId) {
		return userStoryRepo.findById(userStoryId).orElseThrow(() -> new EntityNotFoundException(UserStory.class));
	}

	@Override
	@Transactional
	public void deleteUserStory(long userStoryId) {

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new EntityNotFoundException(UserStory.class));

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);

		userStoryRepo.delete(userStory);

	}
}
