package com.projectpal.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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
import com.projectpal.utils.ProjectUtil;

@RestController
@RequestMapping("/sprint")
public class SprintController {

	@Autowired
	public SprintController(SprintRepository sprintRepo) {

		this.sprintRepo = sprintRepo;
	}

	private final SprintRepository sprintRepo;

	@GetMapping("/list")
	public ResponseEntity<List<Sprint>> getsprintList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Sprint> sprints = sprintRepo.findAllByProject(project)
				.orElseThrow(() -> new ResourceNotFoundException("no sprints found"));

		sprints.sort((sprint1, sprint2) -> sprint1.getStartDate().compareTo(sprint2.getStartDate()));

		return ResponseEntity.ok(sprints);
	}

	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createsprint(@RequestBody Sprint sprint) {

		ProjectUtil.onlyProjectOwnerAllowed();

		if (sprint.getName() == null || sprint.getStartDate() == null || sprint.getEndDate() == null)
			throw new BadRequestException("sprint name or startdate or enddate is null");

		if (sprint.getStartDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setProject(ProjectUtil.getProjectNotNull());

		sprintRepo.save(sprint);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/sprint").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PatchMapping("/startdate/{id}")
	@Transactional
	public ResponseEntity<Void> updateStartDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @PathVariable long id) {
		
		ProjectUtil.onlyProjectOwnerAllowed();

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (startDate.isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");
		
		sprint.setStartDate(startDate);
		
		sprintRepo.save(sprint);
		
		return ResponseEntity.status(204).build();
	}

	@PatchMapping("/enddate/{id}")
	@Transactional
	public ResponseEntity<Void> updateEndDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @PathVariable long id) {
		
		ProjectUtil.onlyProjectOwnerAllowed();

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (endDate.isBefore(sprint.getStartDate()))
			throw new BadRequestException("End date is before Start date");
		
		sprint.setEndDate(endDate);
		
		sprintRepo.save(sprint);
		
		return ResponseEntity.status(204).build();
	}

	@PatchMapping("/description/{id}")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestParam String description, @PathVariable long id) {
		ProjectUtil.onlyProjectOwnerAllowed();

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		sprint.setDescription(description);

		sprintRepo.save(sprint);

		return ResponseEntity.status(204).build();
	}

	@PatchMapping("/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		ProjectUtil.onlyProjectOwnerAllowed();

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprint.setProgress(progress);

		sprintRepo.save(sprint);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deletesprint(@PathVariable long id) {

		ProjectUtil.onlyProjectOwnerAllowed();

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprintRepo.delete(sprint);

		return ResponseEntity.status(204).build();
	}
}
