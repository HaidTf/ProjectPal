package com.projectpal.service.userstory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.DBConstants;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.SprintRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.service.cache.CacheConstants;
import com.projectpal.service.cache.UserStoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprintUserStoryServiceImpl implements SprintUserStoryService {

	private final UserStoryRepository userStoryRepo;

	private final SprintRepository sprintRepo;

	private final UserStoryService userStoryService;

	private final UserStoryCacheService userStoryCacheService;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional
	@Override
	public List<UserStory> findUserStoriesBySprintAndProgressListFromDbOrCache(long sprintId, Set<Progress> progress,
			Sort sort) {

		Sprint sprint = sprintRepo
				.findByIdAndProject(sprintId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

		Optional<List<UserStory>> userStories = Optional.empty();

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			userStories = userStoryCacheService.getListFromCache(CacheConstants.SPRINT_USERSTORY_CACHE, sprint.getId());
			if (userStories.isEmpty()) {
				userStories = Optional.of(userStoryRepo.findAllBySprintAndProgressIn(sprint, progress));
				userStoryCacheService.putListInCache(CacheConstants.SPRINT_USERSTORY_CACHE, sprint.getId(),
						userStories.get());
			}

			userStoryService.sort(userStories.get(), sort);

		} else {
			userStories = Optional.of(this.findUserStoriesBySprintAndProgressFromDb(sprint, progress, sort));
		}

		return userStories.get();

	}

	@Transactional(readOnly = true)
	@Override
	public List<UserStory> findUserStoriesBySprintAndProgressFromDb(Sprint sprint, Set<Progress> progress, Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return userStoryRepo.findAllBySprint(sprint, sort);
		}
		default -> {
			return userStoryRepo.findAllBySprintAndProgressIn(sprint, progress, sort);
		}

		}

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void addUserStoryToSprint(long userStoryId, long sprintId) {

		User currentUser = authenticationContextFacadeImpl.getCurrentUser();

		Sprint sprint = sprintRepo.findByIdAndProject(sprintId, currentUser.getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

		if (userStoryRepo.countBySprintId(sprint.getId()) > DBConstants.MAX_NUMBER_OF_USERSTORIES)
			throw new ConflictException("Reached maximum number of userstories allowed in a sprint");

		userStory.setSprint(sprint);

		userStoryRepo.save(userStory);

		if (userStory.getProgress() == Progress.TODO || userStory.getProgress() == Progress.INPROGRESS)
			userStoryCacheService.addObjectToListInCache(CacheConstants.SPRINT_USERSTORY_CACHE, sprint.getId(), userStory);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void removeUserStoryFromSprint(long userStoryId, long sprintId) {

		User currentUser = authenticationContextFacadeImpl.getCurrentUser();

		Sprint sprint = sprintRepo.findByIdAndProject(sprintId, currentUser.getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Sprint not found"));

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

		if (userStory.getSprint().getId() != sprint.getId()) {
			throw new BadRequestException("They userStory is not in the specified sprint");
		}

		userStory.setSprint(null);

		userStoryRepo.save(userStory);

		if (userStory.getProgress() == Progress.TODO || userStory.getProgress() == Progress.INPROGRESS)
			userStoryCacheService.evictCache(CacheConstants.SPRINT_USERSTORY_CACHE, sprint.getId());

	}

}
