package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.projectpal.entity.Sprint;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.dto.request.DateUpdateRequest;
import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.ProgressUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.exception.BadRequestException;
import com.projectpal.service.SprintService;
import com.projectpal.utils.ProjectMembershipValidationUtil;
import com.projectpal.utils.SortValidationUtil;
import com.projectpal.utils.UserEntityAccessValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/project/sprints")
public class SprintController {
	@Autowired
	public SprintController(SprintService sprintService) {
		this.sprintService = sprintService;
	}

	private final SprintService sprintService;

	@GetMapping("/{sprintId}")
	public ResponseEntity<Sprint> getSprint(@AuthenticationPrincipal User currentUser, @PathVariable long sprintId) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		Sprint sprint = sprintService.findSprintById(sprintId);

		UserEntityAccessValidationUtil.verifyUserAccessToSprint(currentUser, sprint);

		return ResponseEntity.ok(sprint);

	}

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<Sprint>> getSprints(@AuthenticationPrincipal User currentUser,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "start-date", direction = Sort.Direction.DESC) Sort sort) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		SortValidationUtil.validateSortObjectProperties(Sprint.ALLOWED_SORT_PROPERTIES, sort);

		List<Sprint> sprints = sprintService.findSprintsByProjectAndProgressFromDbOrCache(currentUser.getProject(),
				progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<Sprint>(sprints));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Sprint> createSprint(@AuthenticationPrincipal User currentUser,
			@Valid @RequestBody Sprint sprint) {

		Project project = currentUser.getProject();

		if (sprint.getStartDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprintService.createSprint(project, sprint);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/sprints/" + sprint.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(sprint);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/start-date")
	public ResponseEntity<Void> updateStartDate(@RequestBody @Valid DateUpdateRequest startDateUpdateRequest,
			@PathVariable long id) {

		sprintService.updateStartDate(id, startDateUpdateRequest.getDate());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/end-date")
	public ResponseEntity<Void> updateEndDate(@RequestBody @Valid DateUpdateRequest endDateUpdateRequest,
			@PathVariable long id) {

		sprintService.updateEndDate(id, endDateUpdateRequest.getDate());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long id) {

		sprintService.updateDescription(id, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressUpdateRequest progressUpdateRequest,
			@PathVariable long id) {

		sprintService.updateProgress(id, progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSprint(@PathVariable long id) {

		sprintService.deleteSprint(id);

		return ResponseEntity.status(204).build();
	}
}
