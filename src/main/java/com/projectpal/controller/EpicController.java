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

import com.projectpal.dto.mapper.EpicMapper;
import com.projectpal.dto.request.DescriptionDto;
import com.projectpal.dto.request.PriorityDto;
import com.projectpal.dto.request.ProgressDto;
import com.projectpal.dto.request.entity.EpicCreationDto;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Progress;
import com.projectpal.service.epic.EpicService;
import com.projectpal.validation.ProjectMembershipValidator;
import com.projectpal.validation.SortObjectValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/project/epics")
@RequiredArgsConstructor
@Slf4j
public class EpicController {

	private final EpicService epicService;

	private final EpicMapper epicMapper;

	@GetMapping("/{epicId}")
	public ResponseEntity<Epic> getEpic(@AuthenticationPrincipal User currentUser, @PathVariable long epicId) {

		log.debug("API:GET/api/project/epics/{} invoked", epicId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		Epic epic = epicService.findEpicByIdAndProject(epicId, currentUser.getProject());

		return ResponseEntity.ok(epic);

	}

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<Epic>> getEpics(@AuthenticationPrincipal User currentUser,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		log.debug("API:GET/api/project/epics invoked");

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		SortObjectValidator.validateSortObjectProperties(Epic.ALLOWED_SORT_PROPERTIES, sort);

		List<Epic> epics = epicService.findEpicsByProjectAndProgressFromDbOrCache(currentUser.getProject(), progress,
				sort);

		return ResponseEntity.ok(new ListHolderResponse<Epic>(epics));

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Epic> createEpic(@AuthenticationPrincipal User currentUser,
			@Valid @RequestBody EpicCreationDto epicCreationDto) {

		log.debug("API:POST/api/project/epics invoked");
		
		Project project = currentUser.getProject();

		Epic epic = epicMapper.toEpic(epicCreationDto);

		epicService.createEpic(project, epic);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/epics/" + epic.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(epic);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/description")
	public ResponseEntity<Void> updateDescription(@RequestBody DescriptionDto descriptionUpdateRequest,
			@PathVariable long id) {

		log.debug("API:PATCH/api/project/epics/{}/description invoked", id);
		
		epicService.updateDescription(id, descriptionUpdateRequest.getDescription());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/priority")
	public ResponseEntity<Void> updatePriority(@RequestBody @Valid PriorityDto priorityHolder, @PathVariable long id) {

		log.debug("API:PATCH/api/project/epics/{}/priority invoked", id);
		
		epicService.updatePriority(id, priorityHolder.getPriority());

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/{id}/progress")
	public ResponseEntity<Void> updateProgress(@RequestBody @Valid ProgressDto progressUpdateRequest,
			@PathVariable long id) {
		
		log.debug("API:PATCH/api/project/epics/{}/progress invoked", id);

		epicService.updateProgress(id, progressUpdateRequest.getProgress());

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEpic(@PathVariable long id) {

		log.debug("API:DELETE/api/project/epics/{} invoked", id);
		
		epicService.deleteEpic(id);

		return ResponseEntity.status(204).build();
	}
}
