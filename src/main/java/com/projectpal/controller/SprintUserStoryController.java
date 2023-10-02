package com.projectpal.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.request.IdHolderRequest;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.service.SprintService;
import com.projectpal.service.SprintUserStoryService;
import com.projectpal.service.UserStoryService;
import com.projectpal.utils.ProjectUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/sprints/{sprintId}/userstories")
public class SprintUserStoryController {

	@Autowired
	public SprintUserStoryController(SprintUserStoryService sprintUserStoryService, SprintService sprintService, UserStoryService userStoryService) {
		this.userStoryService = userStoryService;
		this.sprintUserStoryService = sprintUserStoryService;
		this.sprintService = sprintService;
	}

	private final UserStoryService userStoryService;

	private final SprintUserStoryService sprintUserStoryService;

	private final SprintService sprintService;

	@GetMapping("")
	@Transactional
	public ResponseEntity<ListHolderResponse<UserStory>> getSprintUserStoryList(@PathVariable long sprintId,
			@RequestParam(required = false, defaultValue = "TODO,INPROGRESS") Set<Progress> progress,
			@SortDefault(sort = "priority", direction = Sort.Direction.DESC) Sort sort) {

		Sprint sprint = sprintService.findSprintById(sprintId);

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		List<UserStory> userStories = sprintUserStoryService.findAllBySprintAndProgressListFromDbOrCache(sprint, progress, sort);

		return ResponseEntity.ok(new ListHolderResponse<UserStory>(userStories));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	@Transactional
	public ResponseEntity<Void> addUserStoryToSprint(@PathVariable long sprintId,
			@RequestBody @Valid IdHolderRequest userStoryIdHolder) {

		Project project = ProjectUtil.getProjectNotNull();

		Sprint sprint = sprintService.findSprintById(sprintId);

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		UserStory userStory = userStoryService.findUserStoryById(userStoryIdHolder.getId());

		if (userStory.getEpic().getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		sprintUserStoryService.addUserStoryToSprint(userStory, sprint);

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{userStoryId}")
	@Transactional
	public ResponseEntity<Void> removeUserStoryFromSprint(@PathVariable long sprintId, @PathVariable long userStoryId) {

		Project project = ProjectUtil.getProjectNotNull();

		Sprint sprint = sprintService.findSprintById(sprintId);

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		UserStory userStory = userStoryService.findUserStoryById(userStoryId);

		if (userStory.getSprint().getId() != sprint.getId()) {

			if (userStory.getEpic().getProject().getId() != project.getId())
				throw new ForbiddenException("you are not allowed access to other projects");
			else {
				throw new BadRequestException("They userStory is not in the specified sprint");
			}
		}

		sprintUserStoryService.removeUserStoryFromSprint(userStory, sprint);

		return ResponseEntity.status(204).build();
	}
}
