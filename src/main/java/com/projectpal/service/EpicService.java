package com.projectpal.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.utils.UserEntityAccessValidationUtil;

@Service
public class EpicService {

	public EpicService(EpicRepository epicRepo, UserStoryRepository userStoryRepo,
			@Qualifier("epicCacheService") CacheService<Epic> epicCacheService,
			@Qualifier("epicCacheService") CacheService<UserStory> userStoryCacheService,
			AuthenticationContextFacade authenticationContextFacadeImpl) {
		this.epicRepo = epicRepo;
		this.userStoryRepo = userStoryRepo;
		this.authenticationContextFacadeImpl = authenticationContextFacadeImpl;
		this.epicCacheService = epicCacheService;
		this.userStoryCacheService = userStoryCacheService;
	}

	private final EpicRepository epicRepo;

	private final UserStoryRepository userStoryRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	private final CacheService<Epic> epicCacheService;

	private final CacheService<UserStory> userStoryCacheService;

	public Epic findEpicById(long epicId) {
		return epicRepo.findById(epicId).orElseThrow(() -> new ResourceNotFoundException("Epic does not exist"));
	}

	public List<Epic> findEpicsByProjectAndProgressFromDbOrCache(Project project, Set<Progress> progress, Sort sort) {

		Optional<List<Epic>> epics = Optional.empty();

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			epics = epicCacheService.getObjectsFromCache(Epic.EPIC_CACHE, project.getId());
			if (epics.isEmpty()) {
				epics = epicRepo.findAllByProjectAndProgressList(project, progress);
				epicCacheService.populateCache(Epic.EPIC_CACHE, project.getId(), epics.get());
			}

			this.sort(epics.get(), sort);

		} else {
			epics = this.findEpicsByProjectAndProgressFromDb(project, progress, sort);
		}

		return epics.get();

	}

	public Optional<List<Epic>> findEpicsByProjectAndProgressFromDb(Project project, Set<Progress> progress,
			Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return epicRepo.findAllByProject(project, sort);
		}
		default -> {
			return epicRepo.findAllByProjectAndProgressList(project, progress, sort);
		}

		}

	}

	public void createEpic(Project project, Epic epic) {

		if (epicRepo.countByProjectId(project.getId()) > Project.MAX_NUMBER_OF_EPICS)
			throw new ConflictException("Reached maximum number of epics allowed in a project");

		epic.setProject(project);

		epicRepo.save(epic);

		epicCacheService.addObjectToCache(Epic.EPIC_CACHE, project.getId(), epic);

	}

	public void updateDescription(long epicId, String description) {

		Epic epic = this.findEpicById(epicId);

		UserEntityAccessValidationUtil.verifyUserAccessToEpic(authenticationContextFacadeImpl.getCurrentUser(), epic);

		epic.setDescription(description);

		epicRepo.save(epic);

		epicCacheService.evictListFromCache(Epic.EPIC_CACHE, epic.getProject().getId());

	}

	public void updatePriority(long epicId, int priority) {

		Epic epic = this.findEpicById(epicId);

		UserEntityAccessValidationUtil.verifyUserAccessToEpic(authenticationContextFacadeImpl.getCurrentUser(), epic);

		epic.setPriority(priority);

		epicRepo.save(epic);

		epicCacheService.evictListFromCache(Epic.EPIC_CACHE, epic.getProject().getId());

	}

	public void updateProgress(long epicId, Progress progress) {

		Epic epic = this.findEpicById(epicId);

		UserEntityAccessValidationUtil.verifyUserAccessToEpic(authenticationContextFacadeImpl.getCurrentUser(), epic);

		epic.setProgress(progress);

		epicRepo.save(epic);

		epicCacheService.evictListFromCache(Epic.EPIC_CACHE, epic.getProject().getId());

	}

	public void deleteEpic(long epicId) {

		Epic epic = this.findEpicById(epicId);

		UserEntityAccessValidationUtil.verifyUserAccessToEpic(authenticationContextFacadeImpl.getCurrentUser(), epic);

		epicCacheService.evictListFromCache(Epic.EPIC_CACHE, epic.getProject().getId());

		List<UserStory> userStories = userStoryCacheService
				.getObjectsFromCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId()).orElseGet(() -> userStoryRepo
						.findAllByEpicAndProgressList(epic, Set.of(Progress.TODO, Progress.INPROGRESS)));

		for (UserStory userStory : userStories) {
			if (userStory.getSprint() != null)
				userStoryCacheService.evictListFromCache(UserStory.SPRINT_USERSTORY_CACHE,
						userStory.getSprint().getId());
		}
		userStoryCacheService.evictListFromCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId());

		epicRepo.delete(epic);

	}

	protected void sort(List<Epic> epics, Sort sort) {

		Comparator<Epic> combinedComparator = null;

		for (Sort.Order order : sort) {

			Comparator<Epic> currentComparator;

			switch (order.getProperty()) {
			case "priority":
				currentComparator = Comparator.comparing(Epic::getPriority);
				break;
			case "creationDate":
				currentComparator = Comparator.comparing(Epic::getCreationDate);
				break;
			default:
				currentComparator = Comparator.comparing(Epic::getPriority);
				break;
			}

			if (order.getDirection() == Sort.Direction.DESC) {
				currentComparator = currentComparator.reversed();
			}

			combinedComparator = (combinedComparator == null) ? currentComparator
					: combinedComparator.thenComparing(currentComparator);
		}

		if (combinedComparator != null) {
			epics.sort(combinedComparator);
		}

	}

}
