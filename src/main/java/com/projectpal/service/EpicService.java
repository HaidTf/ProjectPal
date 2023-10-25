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

import com.projectpal.entity.DBConstants;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.service.cache.CacheConstants;
import com.projectpal.service.cache.EpicCacheService;
import com.projectpal.service.cache.UserStoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EpicService {

	private final EpicRepository epicRepo;

	private final UserStoryRepository userStoryRepo;
	
	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	private final EpicCacheService epicCacheService;

	private final UserStoryCacheService userStoryCacheService;

	@Transactional(readOnly = true)
	public Epic findEpicById(long epicId) {
		return epicRepo.findById(epicId).orElseThrow(() -> new ResourceNotFoundException("Epic does not exist"));
	}

	@Transactional(readOnly = true)
	public Epic findEpicByIdAndProject(long epicId, Project project) {
		return epicRepo.findByIdAndProject(epicId, project)
				.orElseThrow(() -> new ResourceNotFoundException("Epic not found"));
	}

	@Transactional
	public List<Epic> findEpicsByProjectAndProgressFromDbOrCache(Project project, Set<Progress> progress, Sort sort) {

		List<Epic> epics = new ArrayList<>(0);

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			Optional<List<Epic>> cacheEpics = epicCacheService.getObjectsFromCache(CacheConstants.EPIC_CACHE, project.getId());
			if (cacheEpics.isEmpty()) {
				epics = epicRepo.findAllByProjectAndProgressIn(project, progress);
				epicCacheService.populateCache(CacheConstants.EPIC_CACHE, project.getId(), epics);
			}

			this.sort(epics, sort);

		} else {
			epics = this.findEpicsByProjectAndProgressFromDb(project, progress, sort);
		}

		return epics;

	}

	@Transactional(readOnly = true)
	public List<Epic> findEpicsByProjectAndProgressFromDb(Project project, Set<Progress> progress, Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return epicRepo.findAllByProject(project, sort);
		}
		default -> {
			return epicRepo.findAllByProjectAndProgressIn(project, progress, sort);
		}

		}

	}

	@Transactional
	public void createEpic(Project project, Epic epic) {

		if (epicRepo.countByProjectId(project.getId()) > DBConstants.MAX_NUMBER_OF_EPICS)
			throw new ConflictException("Reached maximum number of epics allowed in a project");

		epic.setProgress(Progress.TODO);

		epic.setProject(project);

		epicRepo.save(epic);

		epicCacheService.addObjectToCache(CacheConstants.EPIC_CACHE, project.getId(), epic);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateDescription(long epicId, String description) {

		Epic epic = epicRepo.findByIdAndProject(epicId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Epic not found"));

		epic.setDescription(description);

		epicRepo.save(epic);

		epicCacheService.evictListFromCache(CacheConstants.EPIC_CACHE, epic.getProject().getId());

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updatePriority(long epicId, int priority) {

		Epic epic = epicRepo.findByIdAndProject(epicId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Epic not found"));

		epic.setPriority(priority);

		epicRepo.save(epic);

		epicCacheService.evictListFromCache(CacheConstants.EPIC_CACHE, epic.getProject().getId());

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateProgress(long epicId, Progress progress) {

		Epic epic = epicRepo.findByIdAndProject(epicId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Epic not found"));

		epic.setProgress(progress);

		epicRepo.save(epic);

		epicCacheService.evictListFromCache(CacheConstants.EPIC_CACHE, epic.getProject().getId());

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void deleteEpic(long epicId) {

		Epic epic = epicRepo.findByIdAndProject(epicId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Epic not found"));

		epicCacheService.evictListFromCache(CacheConstants.EPIC_CACHE, epic.getProject().getId());

		List<UserStory> userStories = userStoryCacheService
				.getObjectsFromCache(CacheConstants.EPIC_USERSTORY_CACHE, epic.getId()).orElseGet(() -> userStoryRepo
						.findAllByEpicAndProgressIn(epic, Set.of(Progress.TODO, Progress.INPROGRESS)));

		for (UserStory userStory : userStories) {
			if (userStory.getSprint() != null)
				userStoryCacheService.evictListFromCache(CacheConstants.SPRINT_USERSTORY_CACHE,
						userStory.getSprint().getId());
		}
		userStoryCacheService.evictListFromCache(CacheConstants.EPIC_USERSTORY_CACHE, epic.getId());

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
