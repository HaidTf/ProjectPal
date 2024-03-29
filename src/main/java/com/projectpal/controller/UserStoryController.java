package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.projectpal.service.userstory.UserStoryService;
import com.projectpal.dto.mapper.UserStoryMapper;
import com.projectpal.dto.request.DescriptionDto;
import com.projectpal.dto.request.PriorityDto;
import com.projectpal.dto.request.ProgressDto;
import com.projectpal.dto.request.entity.UserStoryCreationDto;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.User;
import com.projectpal.validation.ProjectMembershipValidator;
import com.projectpal.validation.SortObjectValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/epics")
@RequiredArgsConstructor
@Slf4j
public class UserStoryController {

	private final UserStoryService userStoryService;

	private final UserStoryMapper userStoryMapper;

	@GetMapping("/userstories/{userStoryId}")
	public ResponseEntity<UserStory> getUserStory(@AuthenticationPrincipal User currentUser,
			@PathVariable long userStoryId) {

		log.debug("API:GET/api/epics/userstories/{} invoked", userStoryId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		UserStory userStory = userStoryService.findUserStoryByIdAndEpicProject(userStoryId, currentUser.getProject());

		return ResponseEntity.ok(userStory);

	}

	@GetMapping("/{epicId}/userstories")
	public ResponseEntity<ListHolderResponse<UserStory>> getEpicUserStoryList(@AuthenticationPrincipal User currentUser,
			@PathVariable long epicId,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		log.debug("API:GET/api/epics/{}/userstories invoked", epicId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		SortObjectValidator.validateSortObjectProperties(UserStory.ALLOWED_SORT_PROPERTIES, sort);

		List<UserStory> userStories = userStoryService.findUserStoriesByEpicAndProgressFromDbOrCache(epicId, progress,
				sort);

		return ResponseEntity.ok(new ListHolderResponse<UserStory>(userStories));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/{epicId}/userstories")
	public ResponseEntity<UserStory> createUserStory(@Valid @RequestBody UserStoryCreationDto userStoryCreationDto,
			@PathVariable long epicId) {

		log.debug("API:POST/api/epics/{}/userstories invoked", epicId);
		
		UserStory userStory = userStoryMapper.toUserStory(userStoryCreationDto);

		userStoryService.createUserStory(epicId, userStory);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/epics/userstories/" + userStory.getId())
				.build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(userStory);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{UserStoryId}/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionDto descriptionUpdateRequest,
			@PathVariable long userStoryId) {

		log.debug("API:PATCH/api/epics/userstories/{}/description invoked", userStoryId);
		
		userStoryService.updateDescription(userStoryId, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{userStoryId}/priority")
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityDto priorityUpdateRequest,
			@PathVariable long userStoryId) {

		log.debug("API:PATCH/api/epics/userstories/{}/priority invoked", userStoryId);
		
		userStoryService.updatePriority(userStoryId, priorityUpdateRequest.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/userstories/{userStoryId}/progress")
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressDto progressUpdateRequest,
			@PathVariable long userStoryId) {

		log.debug("API:PATCH/api/epics/userstories/{}/progress invoked", userStoryId);
		
		userStoryService.updateProgress(userStoryId, progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/userstories/{userStoryId}")
	public ResponseEntity<Void> deleteUserStory(@PathVariable long userStoryId) {

		log.debug("API:DELETE/api/epics/userstories/{} invoked", userStoryId);
		
		userStoryService.deleteUserStory(userStoryId);

		return ResponseEntity.status(204).build();
	}
}
