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

import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.PriorityUpdateRequest;
import com.projectpal.dto.request.ProgressUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.service.EpicService;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SortValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/project/epics")
public class EpicController {

	@Autowired
	public EpicController(EpicService epicService) {
		this.epicService = epicService;
	}

	private final EpicService epicService;

	@GetMapping("/{epicId}")
	public ResponseEntity<Epic> getEpic(@PathVariable long epicId) {

		Project project = ProjectUtil.getProjectNotNull();

		Epic epic = epicService.findEpicById(epicId);

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("You are not allowed access to other projects");

		return ResponseEntity.ok(epic);

	}

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<Epic>> getEpics(
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		Project project = ProjectUtil.getProjectNotNull();

		SortValidationUtil.validateSortObjectProperties(Epic.ALLOWED_SORT_PROPERTIES, sort);

		List<Epic> epics = epicService.findEpicsByProjectAndProgressFromDbOrCache(project, progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<Epic>(epics));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	@Transactional
	public ResponseEntity<Epic> createEpic(@Valid @RequestBody Epic epic) {

		Project project = ProjectUtil.getProjectNotNull();

		epicService.createEpic(project, epic);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/epics/" + epic.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(epic);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long id) {

		Epic epic = epicService.findEpicById(id);

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of epics from other projects");

		epicService.updateDescription(epic, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/priority")
	@Transactional
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityUpdateRequest priorityHolder,
			@PathVariable long id) {

		Epic epic = epicService.findEpicById(id);

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update priority of epics from other projects");

		epicService.updatePriority(epic, priorityHolder.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressUpdateRequest progressUpdateRequest,
			@PathVariable long id) {

		Epic epic = epicService.findEpicById(id);

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		epicService.updateProgress(epic, progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteEpic(@PathVariable long id) {

		Epic epic = epicService.findEpicById(id);

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		epicService.deleteEpic(epic);

		return ResponseEntity.status(204).build();
	}
}
