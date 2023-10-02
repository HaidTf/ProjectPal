package com.projectpal.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.UserStoryRepository;

@Service
public class UserStoryService {

	@Autowired
	public UserStoryService(UserStoryRepository userStoryRepo, UserStoryCacheService userStoryCacheService) {
		this.userStoryRepo = userStoryRepo;
		this.userStoryCacheService = userStoryCacheService;
	}

	private final UserStoryRepository userStoryRepo;

	private final UserStoryCacheService userStoryCacheService;

	public UserStory findUserStoryById(long userStoryId) {

		return userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("UserStory does not exist"));

	}

	public List<UserStory> findUserStoriesByEpicAndProgressFromDbOrCache(Epic epic, Set<Progress> progress, Sort sort) {

		Optional<List<UserStory>> userStories = Optional.empty();

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			userStories = userStoryCacheService.getObjectsFromCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId());
			if (userStories.isEmpty()) {
				userStories = Optional.of(userStoryRepo.findAllByEpicAndProgressList(epic, progress));
				userStoryCacheService.populateCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId(), userStories.get());
			}

			this.sort(userStories.get(), sort);

		} else {
			userStories = Optional.of(this.findUserStoriesByEpicAndProgressFromDb(epic, progress, sort));
		}

		return userStories.get();

	}

	public List<UserStory> findUserStoriesByEpicAndProgressFromDb(Epic epic, Set<Progress> progress,
			Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return userStoryRepo.findAllByEpic(epic, sort);
		}
		default -> {
			return userStoryRepo.findAllByEpicAndProgressList(epic, progress, sort);
		}

		}

	}

	protected void sort(List<UserStory> userStories, Sort sort) {

		Comparator<UserStory> combinedComparator = null;

		for (Sort.Order order : sort) {

			Comparator<UserStory> currentComparator;

			switch (order.getProperty()) {
			case "priority":
				currentComparator = Comparator.comparing(UserStory::getPriority);
				break;
			case "creation-date":
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

	public void createUserStory(Epic epic, UserStory userStory) {

		if (userStoryRepo.countBySprintId(epic.getId()) > Epic.MAX_NUMBER_OF_USERSTORIES)
			throw new ConflictException("Reached maximum number of userstories allowed in an epic ");
		
		userStory.setEpic(epic);

		userStoryRepo.save(userStory);

		userStoryCacheService.addObjectToCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId(), userStory);

	}

	public void updateDescription(UserStory userStory, String description) {

		userStory.setDescription(description);

		userStoryRepo.save(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);

	}

	public void updatePriority(UserStory userStory, int priority) {

		userStory.setPriority(priority);

		userStoryRepo.save(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);

	}

	public void updateProgress(UserStory userStory, Progress progress) {

		userStory.setProgress(progress);

		userStoryRepo.save(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);
	}

	public void deleteUserStory(UserStory userStory) {

		userStoryRepo.delete(userStory);

		userStoryCacheService.evictCachesWhereUserStoryIsPresent(userStory);
	}

}
