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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.request.PriorityParameterRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.service.CacheService;
import com.projectpal.service.CacheServiceEpicAddOn;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects/epics")
public class EpicController {

	@Autowired
	public EpicController(EpicRepository epicRepo, CacheServiceEpicAddOn cacheServiceEpicAddOn,
			CacheService cacheService) {
		this.epicRepo = epicRepo;
		this.cacheServiceEpicAddOn = cacheServiceEpicAddOn;
		this.cacheService = cacheService;
	}

	private final EpicRepository epicRepo;

	private final CacheServiceEpicAddOn cacheServiceEpicAddOn;

	private final CacheService cacheService;

	// Get NotDone epics

	@GetMapping("/notdone")
	public ResponseEntity<ListHolderResponse<Epic>> getNotDoneEpicList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Epic> epics = cacheServiceEpicAddOn.getNotDoneEpicListFromCacheOrDatabase(project);

		epics.sort((epic1, epic2) -> Integer.compare(epic1.getPriority(), epic2.getPriority()));

		return ResponseEntity.ok(new ListHolderResponse<Epic>(epics));

	}

	// Get all epics

	@GetMapping("/all")
	public ResponseEntity<ListHolderResponse<Epic>> getAllEpicList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Epic> epics = epicRepo.findAllByProject(project).orElse(new ArrayList<Epic>(0));

		epics.sort((epic1, epic2) -> Integer.compare(epic1.getPriority(), epic2.getPriority()));

		return ResponseEntity.ok(new ListHolderResponse<Epic>(epics));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	@Transactional
	public ResponseEntity<Void> createEpic(@Valid @RequestBody Epic epic) {

		Project project = ProjectUtil.getProjectNotNull();

		MaxAllowedUtil.checkMaxAllowedOfEpic(epicRepo.countByProjectId(project.getId()));

		epic.setProject(project);

		epicRepo.save(epic);

		// Redis Cache Update:

		cacheService.addObjectToCache(CacheServiceEpicAddOn.epicListCache, project.getId(), epic);

		// Redis Cache Update End:

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/epic").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of epics from other projects");

		epic.setDescription(description);

		epicRepo.save(epic);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceEpicAddOn.epicListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/priority")
	@Transactional
	public ResponseEntity<Void> updatePriority(/* Request Parameter */ @Valid PriorityParameterRequest priorityHolder,
			@PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update priority of epics from other projects");

		epic.setPriority(priorityHolder.getPriority());

		epicRepo.save(epic);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceEpicAddOn.epicListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		epic.setProgress(progress);

		epicRepo.save(epic);

		// Redis Cache Update:

		cacheService.evictListFromCache(CacheServiceEpicAddOn.epicListCache, project.getId());

		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteEpic(@PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		// Redis Cache Update:

		cacheServiceEpicAddOn.deleteEpicFromCacheAndCascadeDeleteChildren(epic);

		// Redis Cache Update End:

		epicRepo.delete(epic);

		return ResponseEntity.status(204).build();
	}
}
