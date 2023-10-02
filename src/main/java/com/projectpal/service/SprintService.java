package com.projectpal.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.SprintRepository;

@Service
public class SprintService {

	@Autowired
	public SprintService(SprintRepository sprintRepo,
			@Qualifier("sprintCacheService") CacheService<Sprint> sprintCacheService,
			@Qualifier("userStoryCacheService") CacheService<UserStory> userStoryCacheService) {
		this.sprintRepo = sprintRepo;
		this.sprintCacheService = sprintCacheService;
		this.userStoryCacheService = userStoryCacheService;
	}

	private final SprintRepository sprintRepo;

	private final CacheService<Sprint> sprintCacheService;

	private final CacheService<UserStory> userStoryCacheService;

	public Sprint findSprintById(long sprintId) {
		return sprintRepo.findById(sprintId).orElseThrow(() -> new ResourceNotFoundException("Sprint does not exist"));
	}

	public List<Sprint> findSprintsByProjectAndProgressFromDbOrCache(Project project, Set<Progress> progress, Sort sort) {

		Optional<List<Sprint>> sprints = Optional.empty();

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			sprints = sprintCacheService.getObjectsFromCache(Sprint.SPRINT_CACHE, project.getId());
			if (sprints.isEmpty()) {
				sprints = sprintRepo.findAllByProjectAndProgressList(project, progress);
				sprintCacheService.populateCache(Sprint.SPRINT_CACHE, project.getId(), sprints.get());
			}

			this.sort(sprints.get(), sort);

		} else {
			sprints = this.findSprintsByProjectAndProgressFromDb(project, progress, sort);
		}

		return sprints.get();
	}

	public Optional<List<Sprint>> findSprintsByProjectAndProgressFromDb(Project project, Set<Progress> progress,
			Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return sprintRepo.findAllByProject(project, sort);
		}
		default -> {
			return sprintRepo.findAllByProjectAndProgressList(project, progress, sort);
		}

		}
	}

	protected void sort(List<Sprint> sprints, Sort sort) {

		Comparator<Sprint> combinedComparator = null;

		for (Sort.Order order : sort) {

			Comparator<Sprint> currentComparator;

			switch (order.getProperty()) {

			case "start-date":
				currentComparator = Comparator.comparing(Sprint::getStartDate);
				break;
			case "end-date":
				currentComparator = Comparator.comparing(Sprint::getEndDate);
				break;
			case "creation-date":
				currentComparator = Comparator.comparing(Sprint::getCreationDate);
				break;
			default:
				currentComparator = Comparator.comparing(Sprint::getStartDate);
			}

			if (order.getDirection() == Sort.Direction.DESC) {
				currentComparator = currentComparator.reversed();
			}

			combinedComparator = (combinedComparator == null) ? currentComparator
					: combinedComparator.thenComparing(currentComparator);
		}

		if (combinedComparator != null) {
			sprints.sort(combinedComparator);
		}

	}

	public void createSprint(Project project, Sprint sprint) {

		if (sprintRepo.countByProjectId(project.getId()) > Project.MAX_NUMBER_OF_SPRINTS)
			throw new ConflictException("");

		sprint.setProject(project);

		sprintRepo.save(sprint);

		sprintCacheService.addObjectToCache(Sprint.SPRINT_CACHE, project.getId(), sprint);

	}

	public void updateStartDate(Sprint sprint, LocalDate date) {

		sprint.setStartDate(date);

		sprintRepo.save(sprint);

		sprintCacheService.evictListFromCache(Sprint.SPRINT_CACHE, sprint.getProject().getId());

	}

	public void updateEndDate(Sprint sprint, LocalDate date) {

		sprint.setEndDate(date);

		sprintRepo.save(sprint);

		sprintCacheService.evictListFromCache(Sprint.SPRINT_CACHE, sprint.getProject().getId());
	}

	public void updateDescription(Sprint sprint, String description) {

		sprint.setDescription(description);

		sprintRepo.save(sprint);

		sprintCacheService.evictListFromCache(Sprint.SPRINT_CACHE, sprint.getProject().getId());
	}

	public void updateProgress(Sprint sprint, Progress progress) {

		sprint.setProgress(progress);

		sprintRepo.save(sprint);

		sprintCacheService.evictListFromCache(Sprint.SPRINT_CACHE, sprint.getProject().getId());
	}

	public void deleteSprint(Sprint sprint) {

		sprintCacheService.evictListFromCache(Sprint.SPRINT_CACHE, sprint.getProject().getId());

		userStoryCacheService.evictListFromCache(UserStory.SPRINT_USERSTORY_CACHE, sprint.getId());

		sprintRepo.delete(sprint);

	}

}
