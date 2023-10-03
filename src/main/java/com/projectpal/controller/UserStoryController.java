package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
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
import com.projectpal.entity.enums.Progress;
import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.PriorityUpdateRequest;
import com.projectpal.dto.request.ProgressUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.service.EpicService;
import com.projectpal.service.UserStoryService;
import com.projectpal.utils.SecurityContextUtil;
import com.projectpal.utils.SortValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/epics")
public class UserStoryController {

	@Autowired
	public UserStoryController(UserStoryService userStoryService, EpicService epicService) {
		this.userStoryService = userStoryService;
		this.epicService = epicService;
	}

	private final UserStoryService userStoryService;

	private final EpicService epicService;

	@GetMapping("/userstories/{userStoryId}")
	public ResponseEntity<UserStory> getUserStory(@PathVariable long userStoryId) {

		Project project = SecurityContextUtil.getUserProjectNotNull();

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getEpic().getProject().getId() != project.getId())
			throw new ForbiddenException("You are not allowed access to other projects");

		return ResponseEntity.ok(userStory);

	}

	@GetMapping("/{epicId}/userstories")
	@Transactional
	public ResponseEntity<ListHolderResponse<UserStory>> getEpicUserStoryList(@PathVariable long epicId,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		Project project = SecurityContextUtil.getUserProjectNotNull();
		
		Epic epic = epicService.findEpicById(epicId);

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		SortValidationUtil.validateSortObjectProperties(UserStory.ALLOWED_SORT_PROPERTIES, sort);

		List<UserStory> userStories = userStoryService.findUserStoriesByEpicAndProgressFromDbOrCache(epic, progress,
				sort);

		return ResponseEntity.ok(new ListHolderResponse<UserStory>(userStories));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/{epicId}/userstories")
	@Transactional
	public ResponseEntity<UserStory> createUserStory(@Valid @RequestBody UserStory userStory,
			@PathVariable long epicId) {

		Epic epic = epicService.findEpicById(epicId);

		if (epic.getProject().getId() != SecurityContextUtil.getUserProject().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStoryService.createUserStory(epic, userStory);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/epics/userstories/" + userStory.getId())
				.build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(userStory);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{UserStoryId}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long userStoryId) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getEpic().getProject().getId() != SecurityContextUtil.getUserProject().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStoryService.updateDescription(userStory, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{userStoryId}/priority")
	@Transactional
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityUpdateRequest priorityUpdateRequest,
			@PathVariable long userStoryId) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getEpic().getProject().getId() != SecurityContextUtil.getUserProject().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStoryService.updatePriority(userStory, priorityUpdateRequest.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{userStoryId}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressUpdateRequest progressUpdateRequest,
			@PathVariable long userStoryId) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getEpic().getProject().getId() != SecurityContextUtil.getUserProject().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStoryService.updateProgress(userStory, progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/userstories/{userStoryId}")
	@Transactional
	public ResponseEntity<Void> deleteUserStory(@PathVariable long userStoryId) {

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getEpic().getProject().getId() != SecurityContextUtil.getUserProject().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStoryService.deleteUserStory(userStory);

		return ResponseEntity.status(204).build();
	}
}
