package com.projectpal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.entity.UserStory;
import com.projectpal.dto.request.PriorityParameterRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Epic;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.service.CacheService;
import com.projectpal.service.CacheServiceUserStoryAddOn;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/epics")
public class UserStoryController {

	@Autowired
	public UserStoryController(UserStoryRepository userStoryRepo, EpicRepository epicRepo,
			CacheServiceUserStoryAddOn cacheServiceUserStoryAddOn, CacheService cacheService) {
		this.userStoryRepo = userStoryRepo;
		this.epicRepo = epicRepo;
		this.cacheServiceUserStoryAddOn = cacheServiceUserStoryAddOn;
		this.cacheService = cacheService;

	}

	private final EpicRepository epicRepo;

	private final UserStoryRepository userStoryRepo;

	private final CacheServiceUserStoryAddOn cacheServiceUserStoryAddOn;

	private final CacheService cacheService;

	@GetMapping("/{epicId}/userstories")
	@Transactional
	public ResponseEntity<ListHolderResponse<UserStory>> getEpicUserStoryList(@PathVariable long epicId) {

		Epic epic = epicRepo.findById(epicId).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		List<UserStory> userStories = cacheServiceUserStoryAddOn.getEpicUserStoryListFromCacheOrDatabase(epic);

		userStories
				.sort((userStory1, userStory2) -> Integer.compare(userStory1.getPriority(), userStory2.getPriority()));

		return ResponseEntity.ok(new ListHolderResponse<UserStory>(userStories));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/{epicId}/userstories")
	@Transactional
	public ResponseEntity<Void> createUserStory(@Valid @RequestBody UserStory userStory, @PathVariable long epicId) {

		Epic epic = epicRepo.findById(epicId).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		MaxAllowedUtil.checkMaxAllowedOfUserStory(userStoryRepo.countByEpicId(epicId));

		userStory.setEpic(epic);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		cacheService.addObjectToCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, epic.getId(), userStory);

		// Redis Cache Update End:

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/userstory").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{UserStoryId}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long userStoryId) {

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStory.setDescription(description);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, userStory.getEpic().getId());

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache,
				userStory.getSprint().getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{userStoryId}/priority")
	@Transactional
	public ResponseEntity<Void> updatePriority(/* Request Parameter */ @Valid PriorityParameterRequest priorityHolder,
			@PathVariable long userStoryId) {

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStory.setPriority(priorityHolder.getPriority());

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, userStory.getEpic().getId());

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache,
				userStory.getSprint().getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{userStoryId}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long userStoryId) {

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStory.setProgress(progress);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, userStory.getEpic().getId());

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache,
				userStory.getSprint().getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{epicId}/userstories/{userStoryId}")
	@Transactional
	public ResponseEntity<Void> deleteUserStory(@PathVariable long userStoryId) {

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStoryRepo.delete(userStory);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, userStory.getEpic().getId());

		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache,
				userStory.getSprint().getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}
}
