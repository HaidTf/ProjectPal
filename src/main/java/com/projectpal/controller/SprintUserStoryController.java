package com.projectpal.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.IdDto;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.service.userstory.SprintUserStoryService;
import com.projectpal.validation.ProjectMembershipValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sprints/{sprintId}/userstories")
@RequiredArgsConstructor
@Slf4j
public class SprintUserStoryController {

	private final SprintUserStoryService sprintUserStoryService;

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<UserStory>> getSprintUserStoryList(
			@AuthenticationPrincipal User currentUser, @PathVariable long sprintId,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		log.debug("API:GET/api/sprints/{}/userstories invoked", sprintId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		List<UserStory> userStories = sprintUserStoryService
				.findUserStoriesBySprintAndProgressListFromDbOrCache(sprintId, progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<UserStory>(userStories));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Void> addUserStoryToSprint(@PathVariable long sprintId,
			@RequestBody @Valid IdDto userStoryIdHolder) {

		log.debug("API:POST/api/sprints/{}/userstories invoked", sprintId);

		sprintUserStoryService.addUserStoryToSprint(userStoryIdHolder.getId(), sprintId);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{userStoryId}")
	public ResponseEntity<Void> removeUserStoryFromSprint(@PathVariable long sprintId, @PathVariable long userStoryId) {

		log.debug("API:DELETE/api/sprints/{}/userstories/{} invoked", sprintId, userStoryId);

		sprintUserStoryService.removeUserStoryFromSprint(userStoryId, sprintId);

		return ResponseEntity.status(204).build();
	}
}
