package com.projectpal.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.SprintRepository;
import com.projectpal.service.CacheService;
import com.projectpal.service.CacheServiceSprintImpl;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/sprint")
public class SprintController {

	@Autowired
	public SprintController(SprintRepository sprintRepo, CacheService cacheService,
			CacheServiceSprintImpl cacheServiceSprintImpl) {
		this.cacheService = cacheService;
		this.sprintRepo = sprintRepo;
		this.cacheServiceSprintImpl = cacheServiceSprintImpl;

	}

	private final SprintRepository sprintRepo;

	private final CacheService cacheService;

	private final CacheServiceSprintImpl cacheServiceSprintImpl;

	//Get NotDone sprints
	
	@GetMapping("/list/notdone")
	public ResponseEntity<List<Sprint>> getNotDoneSprintList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Sprint> sprints = cacheServiceSprintImpl.getNotDoneSprintListFromCacheOrDatabase(project);

		sprints.sort((sprint1, sprint2) -> sprint1.getStartDate().compareTo(sprint2.getStartDate()));

		return ResponseEntity.ok(sprints);

	}
	
	//Get all sprints
	
	@GetMapping("/list/all")
	public ResponseEntity<List<Sprint>> getAllSprintList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Sprint> sprints = sprintRepo.findAllByProject(project).orElse(new ArrayList<Sprint>(0));

		sprints.sort((sprint1, sprint2) -> sprint1.getStartDate().compareTo(sprint2.getStartDate()));

		return ResponseEntity.ok(sprints);

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createSprint(@Valid @RequestBody Sprint sprint) {

		if (sprint.getStartDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		Project project = ProjectUtil.getProjectNotNull();
		
		MaxAllowedUtil.checkMaxAllowedOfSprint(sprintRepo.countByProjectId(project.getId()));

		sprint.setProject(project);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.addObjectToCache(CacheServiceSprintImpl.sprintListCache, project.getId(), sprint);

		// Redis Cache Update End:

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/sprint").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/startdate/{id}")
	@Transactional
	public ResponseEntity<Void> updateStartDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (startDate.isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setStartDate(startDate);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintImpl.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/enddate/{id}")
	@Transactional
	public ResponseEntity<Void> updateEndDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (endDate.isBefore(sprint.getStartDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setEndDate(endDate);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintImpl.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/description/{id}")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		sprint.setDescription(description);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintImpl.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprint.setProgress(progress);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceSprintImpl.sprintListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteSprint(@PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprintRepo.delete(sprint);

		// Redis Cache Update:

		cacheServiceSprintImpl.deleteSprintFromCacheAndCascadeDeleteChildren(sprint);

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}
}
