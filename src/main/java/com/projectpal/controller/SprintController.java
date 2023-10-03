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

import com.projectpal.entity.Sprint;
import com.projectpal.entity.enums.Progress;
import com.projectpal.dto.request.DateUpdateRequest;
import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.ProgressUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.service.SprintService;
import com.projectpal.utils.SecurityContextUtil;
import com.projectpal.utils.SortValidationUtil;

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
	public ResponseEntity<Sprint> getSprint(@PathVariable long sprintId) {

		Project project = SecurityContextUtil.getUserProjectNotNull();

		Sprint sprint = sprintService.findSprintById(sprintId);

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("You are not allowed access to other projects");

		return ResponseEntity.ok(sprint);

	}

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<Sprint>> getSprints(
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "start-date", direction = Sort.Direction.DESC) Sort sort) {

		Project project = SecurityContextUtil.getUserProjectNotNull();

		SortValidationUtil.validateSortObjectProperties(Sprint.ALLOWED_SORT_PROPERTIES, sort);

		List<Sprint> sprints = sprintService.findSprintsByProjectAndProgressFromDbOrCache(project, progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<Sprint>(sprints));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	@Transactional
	public ResponseEntity<Sprint> createSprint(@Valid @RequestBody Sprint sprint) {

		Project project = SecurityContextUtil.getUserProject();

		if (sprint.getStartDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprintService.createSprint(project, sprint);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/sprints/" + sprint.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(sprint);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/start-date")
	@Transactional
	public ResponseEntity<Void> updateStartDate(@RequestBody @Valid DateUpdateRequest startDateUpdateRequest,
			@PathVariable long id) {

		Sprint sprint = sprintService.findSprintById(id);

		Project project = SecurityContextUtil.getUserProject();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (startDateUpdateRequest.getDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprintService.updateStartDate(sprint, startDateUpdateRequest.getDate());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/end-date")
	@Transactional
	public ResponseEntity<Void> updateEndDate(@RequestBody @Valid DateUpdateRequest endDateUpdateRequest,
			@PathVariable long id) {

		Sprint sprint = sprintService.findSprintById(id);

		Project project = SecurityContextUtil.getUserProject();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (endDateUpdateRequest.getDate().isBefore(sprint.getStartDate()))
			throw new BadRequestException("End date is before Start date");

		sprintService.updateEndDate(sprint,endDateUpdateRequest.getDate());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long id) {

		Sprint sprint = sprintService.findSprintById(id);

		Project project = SecurityContextUtil.getUserProject();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		sprintService.updateDescription(sprint,descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressUpdateRequest progressUpdateRequest,
			@PathVariable long id) {

		Sprint sprint = sprintService.findSprintById(id);

		Project project = SecurityContextUtil.getUserProject();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprintService.updateProgress(sprint,progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteSprint(@PathVariable long id) {

		Sprint sprint = sprintService.findSprintById(id);

		Project project = SecurityContextUtil.getUserProject();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprintService.deleteSprint(sprint);

		return ResponseEntity.status(204).build();
	}
}
