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

import com.projectpal.entity.Sprint;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.client.BadRequestException;
import com.projectpal.dto.mapper.SprintMapper;
import com.projectpal.dto.request.DateDto;
import com.projectpal.dto.request.DescriptionDto;
import com.projectpal.dto.request.ProgressDto;
import com.projectpal.dto.request.entity.SprintCreationDto;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.service.sprint.SprintService;
import com.projectpal.validation.ProjectMembershipValidator;
import com.projectpal.validation.SortObjectValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/project/sprints")
@RequiredArgsConstructor
@Slf4j
public class SprintController {

	private final SprintService sprintService;

	private final SprintMapper sprintMapper;

	@GetMapping("/{sprintId}")
	public ResponseEntity<Sprint> getSprint(@AuthenticationPrincipal User currentUser, @PathVariable long sprintId) {

		log.debug("API:GET/api/project/sprints/{} invoked", sprintId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		Sprint sprint = sprintService.findSprintByIdAndproject(sprintId, currentUser.getProject());

		return ResponseEntity.ok(sprint);

	}

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<Sprint>> getSprints(@AuthenticationPrincipal User currentUser,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "startDate", direction = Sort.Direction.DESC) Sort sort) {

		log.debug("API:GET/api/project/sprints invoked");
		
		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		SortObjectValidator.validateSortObjectProperties(Sprint.ALLOWED_SORT_PROPERTIES, sort);

		List<Sprint> sprints = sprintService.findSprintsByProjectAndProgressFromDbOrCache(currentUser.getProject(),
				progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<Sprint>(sprints));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Sprint> createSprint(@AuthenticationPrincipal User currentUser,
			@Valid @RequestBody SprintCreationDto sprintCreationDto) {

		log.debug("API:POST/api/project/sprints invoked");
		
		Project project = currentUser.getProject();

		Sprint sprint = sprintMapper.toSprint(sprintCreationDto);

		if (sprint.getStartDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprintService.createSprint(project, sprint);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/sprints/" + sprint.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(sprint);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/start-date")
	public ResponseEntity<Void> updateStartDate(@RequestBody @Valid DateDto startDateUpdateRequest,
			@PathVariable long id) {

		log.debug("API:PATCH/api/project/sprints/{}/start-date invoked", id);
		
		sprintService.updateStartDate(id, startDateUpdateRequest.getDate());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/end-date")
	public ResponseEntity<Void> updateEndDate(@RequestBody @Valid DateDto endDateUpdateRequest, @PathVariable long id) {

		log.debug("API:PATCH/api/project/sprints/{}/end-date invoked", id);
		
		sprintService.updateEndDate(id, endDateUpdateRequest.getDate());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionDto descriptionUpdateRequest,
			@PathVariable long id) {

		log.debug("API:PATCH/api/project/sprints/{}/description invoked", id);
		
		sprintService.updateDescription(id, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressDto progressUpdateRequest,
			@PathVariable long id) {

		log.debug("API:PATCH/api/project/sprints/{}/progress invoked", id);
		
		sprintService.updateProgress(id, progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSprint(@PathVariable long id) {

		log.debug("API:DELETE/api/project/sprints/{} invoked", id);
		
		sprintService.deleteSprint(id);

		return ResponseEntity.status(204).build();
	}
}
