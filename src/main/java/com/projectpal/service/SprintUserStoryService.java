package com.projectpal.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ConflictException;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.utils.UserEntityAccessValidationUtil;

@Service
public class SprintUserStoryService {

	@Autowired
	public SprintUserStoryService(UserStoryRepository userStoryRepo, UserStoryCacheService userStoryCacheService,
			UserStoryService userStoryService, SprintService sprintService,
			AuthenticationContextFacade authenticationContextFacadeImpl) {
		this.userStoryRepo = userStoryRepo;
		this.userStoryService = userStoryService;
		this.sprintService = sprintService;
		this.userStoryCacheService = userStoryCacheService;
		this.authenticationContextFacadeImpl = authenticationContextFacadeImpl;
	}

	private final UserStoryRepository userStoryRepo;

	private final UserStoryService userStoryService;

	private final SprintService sprintService;

	private final UserStoryCacheService userStoryCacheService;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	public List<UserStory> findUserStoriesBySprintAndProgressListFromDbOrCache(long sprintId, Set<Progress> progress,
			Sort sort) {

		Sprint sprint = sprintService.findSprintById(sprintId);

		UserEntityAccessValidationUtil.verifyUserAccessToSprint(authenticationContextFacadeImpl.getCurrentUser(),
				sprint);

		Optional<List<UserStory>> userStories = Optional.empty();

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			userStories = userStoryCacheService.getObjectsFromCache(UserStory.SPRINT_USERSTORY_CACHE, sprint.getId());
			if (userStories.isEmpty()) {
				userStories = Optional.of(userStoryRepo.findAllBySprintAndProgressList(sprint, progress));
				userStoryCacheService.populateCache(UserStory.SPRINT_USERSTORY_CACHE, sprint.getId(),
						userStories.get());
			}

			userStoryService.sort(userStories.get(), sort);

		} else {
			userStories = Optional.of(this.findUserStoriesBySprintAndProgressFromDb(sprint, progress, sort));
		}

		return userStories.get();

	}

	public List<UserStory> findUserStoriesBySprintAndProgressFromDb(Sprint sprint, Set<Progress> progress, Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return userStoryRepo.findAllBySprint(sprint, sort);
		}
		default -> {
			return userStoryRepo.findAllBySprintAndProgressList(sprint, progress, sort);
		}

		}

	}

	public void addUserStoryToSprint(long userStoryId, long sprintId) {

		Sprint sprint = sprintService.findSprintById(sprintId);

		UserEntityAccessValidationUtil.verifyUserAccessToSprint(authenticationContextFacadeImpl.getCurrentUser(),
				sprint);

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		UserEntityAccessValidationUtil.verifyUserAccessToUserStory(authenticationContextFacadeImpl.getCurrentUser(),
				userStory);

		if (userStoryRepo.countBySprintId(sprint.getId()) > Sprint.MAX_NUMBER_OF_USERSTORIES)
			throw new ConflictException("Reached maximum number of userstories allowed in a sprint");

		userStory.setSprint(sprint);

		userStoryRepo.save(userStory);

		if (userStory.getProgress() == Progress.TODO || userStory.getProgress() == Progress.INPROGRESS)
			userStoryCacheService.addObjectToCache(UserStory.SPRINT_USERSTORY_CACHE, sprint.getId(), userStory);

	}

	public void removeUserStoryFromSprint(long userStoryId, long sprintId) {

		Sprint sprint = sprintService.findSprintById(sprintId);

		UserEntityAccessValidationUtil.verifyUserAccessToSprint(authenticationContextFacadeImpl.getCurrentUser(),
				sprint);

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getSprint().getId() != sprint.getId()) {

			UserEntityAccessValidationUtil.verifyUserAccessToUserStory(authenticationContextFacadeImpl.getCurrentUser(),
					userStory);

			throw new BadRequestException("They userStory is not in the specified sprint");
		}

		userStory.setSprint(null);

		userStoryRepo.save(userStory);

		if (userStory.getProgress() == Progress.TODO || userStory.getProgress() == Progress.INPROGRESS)
			userStoryCacheService.evictListFromCache(UserStory.SPRINT_USERSTORY_CACHE, sprint.getId());

	}

}
