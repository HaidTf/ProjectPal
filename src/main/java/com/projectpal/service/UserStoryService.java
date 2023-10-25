package com.projectpal.service;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.service.cache.UserStoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserStoryService {

	private final UserStoryRepository userStoryRepo;

	private final EpicRepository epicRepo;

	private final UserStoryCacheService userStoryCacheService;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true)
	public UserStory findUserStoryById(long userStoryId) {

		return userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

	}

	@Transactional(readOnly = true)
	public UserStory findUserStoryByIdAndEpicProject(long userStoryId, Project project) {

		return userStoryRepo.findByIdAndEpicProject(userStoryId, project)
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

	}

	@Transactional
	public List<UserStory> findUserStoriesByEpicAndProgressFromDbOrCache(long epicId, Set<Progress> progress,
			Sort sort) {

		Epic epic = epicRepo.findByIdAndProject(epicId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Epic not found"));

		List<UserStory> userStories = new ArrayList<UserStory>();

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			Optional<List<UserStory>> cacheUserStories = userStoryCacheService
					.getObjectsFromCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId());
			if (cacheUserStories.isEmpty()) {
				userStories = userStoryRepo.findAllByEpicAndProgressIn(epic, progress);
				userStoryCacheService.populateCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId(), userStories);
			}

			this.sort(userStories, sort);

		} else {
			userStories = this.findUserStoriesByEpicAndProgressFromDb(epic, progress, sort);
		}

		return userStories;

	}

	@Transactional(readOnly = true)
	public List<UserStory> findUserStoriesByEpicAndProgressFromDb(Epic epic, Set<Progress> progress, Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return userStoryRepo.findAllByEpic(epic, sort);
		}
		default -> {
			return userStoryRepo.findAllByEpicAndProgressIn(epic, progress, sort);
		}

		}

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void createUserStory(long epicId, UserStory userStory) {

		Epic epic = epicRepo.findByIdAndProject(epicId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Epic not found"));

		if (userStoryRepo.countBySprintId(epic.getId()) > Epic.MAX_NUMBER_OF_USERSTORIES)
			throw new ConflictException("Reached maximum number of userstories allowed in an epic ");

		userStory.setEpic(epic);

		userStoryRepo.save(userStory);

		userStoryCacheService.addObjectToCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId(), userStory);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateDescription(long userStoryId, String description) {

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

		userStory.setDescription(description);

		userStoryRepo.save(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updatePriority(long userStoryId, int priority) {

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

		userStory.setPriority(priority);

		userStoryRepo.save(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateProgress(long userStoryId, Progress progress) {

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

		userStory.setProgress(progress);

		userStoryRepo.save(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void deleteUserStory(long userStoryId) {

		UserStory userStory = userStoryRepo
				.findByIdAndEpicProject(userStoryId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

		userStoryRepo.delete(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);
	}

	protected void sort(List<UserStory> userStories, Sort sort) {

		Comparator<UserStory> combinedComparator = null;

		for (Sort.Order order : sort) {

			Comparator<UserStory> currentComparator;

			switch (order.getProperty()) {
			case "priority":
				currentComparator = Comparator.comparing(UserStory::getPriority);
				break;
			case "creationDate":
				currentComparator = Comparator.comparing(UserStory::getCreationDate);
				break;
			default:
				currentComparator = Comparator.comparing(UserStory::getPriority);
				break;
			}

			if (order.getDirection() == Sort.Direction.DESC) {
				currentComparator = currentComparator.reversed();
			}

			combinedComparator = (combinedComparator == null) ? currentComparator
					: combinedComparator.thenComparing(currentComparator);
		}

		if (combinedComparator != null) {
			userStories.sort(combinedComparator);
		}

	}
}
