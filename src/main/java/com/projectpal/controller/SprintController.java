package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.entity.Sprint;
import com.projectpal.dto.request.DateUpdateRequest;
import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.ProgressUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.SprintRepository;
import com.projectpal.service.CacheService;
import com.projectpal.service.CacheServiceSprintAddOn;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects/sprints")
public class SprintController {

	@Autowired
	public SprintController(SprintRepository sprintRepo, CacheService cacheService,
			CacheServiceSprintAddOn cacheServiceSprintAddOn) {
		this.cacheService = cacheService;
		this.sprintRepo = sprintRepo;
		this.cacheServiceSprintAddOn = cacheServiceSprintAddOn;

	}

	private final SprintRepository sprintRepo;

	private final CacheService cacheService;

	private final CacheServiceSprintAddOn cacheServiceSprintAddOn;

	@GetMapping("/{sprintId}")
	public ResponseEntity<Sprint> getSprint(@PathVariable long sprintId) {

		Project project = ProjectUtil.getProjectNotNull();

		Sprint sprint = sprintRepo.getReferenceById(sprintId);

		try {
			if (sprint.getProject().getId() != project.getId())
				throw new ForbiddenException("You are not allowed access to other projects");
		} catch (EntityNotFoundException ex) {
			throw new ResourceNotFoundException("Sprint does not exist");
		}

		return ResponseEntity.ok(sprint);

	}

	// Get NotDone sprints

	@GetMapping("/notdone")
	public ResponseEntity<ListHolderResponse<Sprint>> getNotDoneSprintList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Sprint> sprints = cacheServiceSprintAddOn.getNotDoneSprintListFromCacheOrDatabase(project);

		sprints.sort((sprint1, sprint2) -> sprint1.getStartDate().compareTo(sprint2.getStartDate()));

		return ResponseEntity.ok(new ListHolderResponse<Sprint>(sprints));

	}

	// Get all sprints

	@GetMapping("/all")
	public ResponseEntity<ListHolderResponse<Sprint>> getAllSprintList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Sprint> sprints = sprintRepo.findAllByProject(project).orElse(new ArrayList<Sprint>(0));

		sprints.sort((sprint1, sprint2) -> sprint1.getStartDate().compareTo(sprint2.getStartDate()));

		return ResponseEntity.ok(new ListHolderResponse<Sprint>(sprints));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	@Transactional
	public ResponseEntity<Sprint> createSprint(@Valid @RequestBody Sprint sprint) {

		if (sprint.getStartDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		Project project = ProjectUtil.getProjectNotNull();

		MaxAllowedUtil.checkMaxAllowedOfSprint(sprintRepo.countByProjectId(project.getId()));

		sprint.setProject(project);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.addObjectToCache(CacheServiceSprintAddOn.sprintListCache, project.getId(), sprint);

		// Redis Cache Update End:

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/projects/sprints/" + sprint.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(sprint);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/startdate")
	@Transactional
	public ResponseEntity<Void> updateStartDate(@RequestBody @Valid DateUpdateRequest startDateUpdateRequest, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (startDateUpdateRequest.getDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setStartDate(startDateUpdateRequest.getDate());

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintAddOn.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/enddate")
	@Transactional
	public ResponseEntity<Void> updateEndDate(@RequestBody @Valid DateUpdateRequest endDateUpdateRequest,
			@PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (endDateUpdateRequest.getDate().isBefore(sprint.getStartDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setEndDate(endDateUpdateRequest.getDate());

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintAddOn.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		sprint.setDescription(descriptionUpdateRequest.getDescription());

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintAddOn.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressUpdateRequest progressUpdateRequest,
			@PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprint.setProgress(progressUpdateRequest.getProgress());

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintAddOn.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteSprint(@PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprintRepo.delete(sprint);

		// Redis Cache Update:

		cacheServiceSprintAddOn.deleteSprintFromCacheAndCascadeDeleteChildren(sprint);

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}
}
