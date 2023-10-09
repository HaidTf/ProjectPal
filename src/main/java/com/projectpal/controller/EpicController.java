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

import com.projectpal.dto.request.DescriptionUpdateRequest;
import com.projectpal.dto.request.PriorityUpdateRequest;
import com.projectpal.dto.request.ProgressUpdateRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.service.EpicService;
import com.projectpal.utils.ProjectMembershipValidationUtil;
import com.projectpal.utils.SortValidationUtil;
import com.projectpal.utils.UserEntityAccessValidationUtil;

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
	public ResponseEntity<Epic> getEpic(@AuthenticationPrincipal User currentUser, @PathVariable long epicId) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		Epic epic = epicService.findEpicById(epicId);

		UserEntityAccessValidationUtil.verifyUserAccessToEpic(currentUser, epic);

		return ResponseEntity.ok(epic);

	}

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<Epic>> getEpics(@AuthenticationPrincipal User currentUser,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		SortValidationUtil.validateSortObjectProperties(Epic.ALLOWED_SORT_PROPERTIES, sort);

		List<Epic> epics = epicService.findEpicsByProjectAndProgressFromDbOrCache(currentUser.getProject(), progress,
				sort);

		return ResponseEntity.ok(new ListHolderResponse<Epic>(epics));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Epic> createEpic(@AuthenticationPrincipal User currentUser, @Valid @RequestBody Epic epic) {

		Project project = currentUser.getProject();

		epicService.createEpic(project, epic);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/epics/" + epic.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(epic);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionUpdateRequest descriptionUpdateRequest,
			@PathVariable long id) {

		epicService.updateDescription(id, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/priority")
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityUpdateRequest priorityHolder,
			@PathVariable long id) {

		epicService.updatePriority(id, priorityHolder.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressUpdateRequest progressUpdateRequest,
			@PathVariable long id) {

		epicService.updateProgress(id, progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEpic(@PathVariable long id) {

		epicService.deleteEpic(id);

		return ResponseEntity.status(204).build();
	}
}
