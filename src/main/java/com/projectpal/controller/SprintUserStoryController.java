package com.projectpal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.SprintRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.service.CacheService;
import com.projectpal.service.CacheServiceUserStoryAddOn;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;

@RestController
@RequestMapping("/sprints/{sprintId}/userstories")
public class SprintUserStoryController {

	@Autowired
	public SprintUserStoryController(SprintRepository sprintRepo, UserStoryRepository userStoryRepo,
			CacheServiceUserStoryAddOn cacheServiceUserStoryAddOn, CacheService cacheService) {
		this.sprintRepo = sprintRepo;
		this.userStoryRepo = userStoryRepo;
		this.cacheServiceUserStoryAddOn = cacheServiceUserStoryAddOn;
		this.cacheService = cacheService;
	}

	private final SprintRepository sprintRepo;

	private final UserStoryRepository userStoryRepo;

	private final CacheServiceUserStoryAddOn cacheServiceUserStoryAddOn;

	private final CacheService cacheService;

	@GetMapping("")
	@Transactional
	public ResponseEntity<ListHolderResponse<UserStory>> getSprintUserStoryList(@PathVariable long sprintId) {

		Sprint sprint = sprintRepo.findById(sprintId)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		List<UserStory> userStories = cacheServiceUserStoryAddOn.getSprintUserStoryListFromCacheOrDatabase(sprint);

		userStories
				.sort((userStory1, userStory2) -> Integer.compare(userStory1.getPriority(), userStory2.getPriority()));

		return ResponseEntity.ok(new ListHolderResponse<UserStory>(userStories));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{userStoryId}/add")
	@Transactional
	public ResponseEntity<Void> addUserStoryToSprint(@PathVariable long sprintId, @PathVariable long userStoryId) {

		Project project = ProjectUtil.getProjectNotNull();

		Sprint sprint = sprintRepo.findById(sprintId)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		MaxAllowedUtil.checkMaxAllowedOfUserStory(userStoryRepo.countBySprintId(sprintId));

		userStory.setSprint(sprint);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		cacheService.addObjectToCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache, sprint.getId(), userStory);

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{userStoryId}/remove")
	@Transactional
	public ResponseEntity<Void> removeUserStoryFromSprint(@PathVariable long sprintId, @PathVariable long userStoryId) {

		Project project = ProjectUtil.getProjectNotNull();

		Sprint sprint = sprintRepo.findById(sprintId)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getSprint().getId() != sprint.getId()) {
			
			if (userStory.getEpic().getProject().getId() != project.getId())
				throw new ForbiddenException("you are not allowed access to other projects");
			else {
				throw new BadRequestException("They userStory is not in the specified sprint");
			}
		}
		MaxAllowedUtil.checkMaxAllowedOfUserStory(userStoryRepo.countBySprintId(sprintId));

		userStory.setSprint(null);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache, sprint.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}
}
