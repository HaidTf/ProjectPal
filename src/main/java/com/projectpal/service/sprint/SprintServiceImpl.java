package com.projectpal.service.sprint;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.DBConstants;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.client.BadRequestException;
import com.projectpal.exception.client.EntityCountLimitException;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.SprintRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.service.cache.CacheConstants;
import com.projectpal.service.cache.CacheService;
import com.projectpal.service.cache.UserStoryCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SprintServiceImpl implements SprintService {

	private final SprintRepository sprintRepo;

	@Qualifier("sprintCacheService")
	private final CacheService<Sprint> sprintCacheService;

	private final UserStoryCacheService userStoryCacheService;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public Sprint findSprintById(long sprintId) {
		return sprintRepo.findById(sprintId).orElseThrow(() -> new EntityNotFoundException(Sprint.class));
	}

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public Sprint findSprintByIdAndproject(long sprintId, Project project) {
		return sprintRepo.findByIdAndProject(sprintId, project)
				.orElseThrow(() -> new EntityNotFoundException(Sprint.class));
	}

	@Transactional
	@Override
	public List<Sprint> findSprintsByProjectAndProgressFromDbOrCache(Project project, Set<Progress> progress,
			Sort sort) {

		List<Sprint> sprints = new ArrayList<Sprint>(0);

		boolean mayBeStoredInCache = (progress.size() == 2 && progress.contains(Progress.TODO)
				&& progress.contains(Progress.INPROGRESS));

		if (mayBeStoredInCache) {

			Optional<List<Sprint>> cacheSprints = sprintCacheService.getListFromCache(CacheConstants.SPRINT_CACHE,
					project.getId());
			if (cacheSprints.isEmpty()) {
				sprints = sprintRepo.findAllByProjectAndProgressIn(project, progress);
				sprintCacheService.putListInCache(CacheConstants.SPRINT_CACHE, project.getId(), sprints);
			}

			this.sort(sprints, sort);

		} else {
			sprints = this.findSprintsByProjectAndProgressFromDb(project, progress, sort);
		}

		return sprints;
	}

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public List<Sprint> findSprintsByProjectAndProgressFromDb(Project project, Set<Progress> progress, Sort sort) {

		switch (progress.size()) {
		case 0, 3 -> {
			return sprintRepo.findAllByProject(project, sort);
		}
		default -> {
			return sprintRepo.findAllByProjectAndProgressIn(project, progress, sort);
		}

		}
	}

	@Transactional
	@Override
	public void createSprint(Project project, Sprint sprint) {

		if (sprintRepo.countByProjectId(project.getId()) > DBConstants.MAX_NUMBER_OF_SPRINTS)
			throw new EntityCountLimitException(Sprint.class);

		sprint.setProgress(Progress.TODO);

		sprint.setProject(project);

		sprintRepo.save(sprint);

		sprintCacheService.addObjectToListInCache(CacheConstants.SPRINT_CACHE, project.getId(), sprint);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void updateStartDate(long sprintId, LocalDate date) {

		Sprint sprint = sprintRepo
				.findByIdAndProject(sprintId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new EntityNotFoundException(Sprint.class));

		if (date.isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setStartDate(date);

		sprintRepo.save(sprint);

		sprintCacheService.evictCache(CacheConstants.SPRINT_CACHE, sprint.getProject().getId());

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void updateEndDate(long sprintId, LocalDate date) {

		Sprint sprint = sprintRepo
				.findByIdAndProject(sprintId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new EntityNotFoundException(Sprint.class));

		if (date.isBefore(sprint.getStartDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setEndDate(date);

		sprintRepo.save(sprint);

		sprintCacheService.evictCache(CacheConstants.SPRINT_CACHE, sprint.getProject().getId());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void updateDescription(long sprintId, String description) {

		Sprint sprint = sprintRepo
				.findByIdAndProject(sprintId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new EntityNotFoundException(Sprint.class));

		sprint.setDescription(description);

		sprintRepo.save(sprint);

		sprintCacheService.evictCache(CacheConstants.SPRINT_CACHE, sprint.getProject().getId());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void updateProgress(long sprintId, Progress progress) {

		Sprint sprint = sprintRepo
				.findByIdAndProject(sprintId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new EntityNotFoundException(Sprint.class));

		sprint.setProgress(progress);

		sprintRepo.save(sprint);

		sprintCacheService.evictCache(CacheConstants.SPRINT_CACHE, sprint.getProject().getId());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void deleteSprint(long sprintId) {

		Sprint sprint = sprintRepo
				.findByIdAndProject(sprintId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new EntityNotFoundException(Sprint.class));

		sprintCacheService.evictCache(CacheConstants.SPRINT_CACHE, sprint.getProject().getId());

		userStoryCacheService.evictCache(CacheConstants.SPRINT_USERSTORY_CACHE, sprint.getId());

		sprintRepo.delete(sprint);

	}

	@Override
	public void sort(List<Sprint> sprints, Sort sort) {

		Comparator<Sprint> combinedComparator = null;

		for (Sort.Order order : sort) {

			Comparator<Sprint> currentComparator;

			switch (order.getProperty()) {

			case "startDate":
				currentComparator = Comparator.comparing(Sprint::getStartDate);
				break;
			case "endDate":
				currentComparator = Comparator.comparing(Sprint::getEndDate);
				break;
			case "creationDate":
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

}
