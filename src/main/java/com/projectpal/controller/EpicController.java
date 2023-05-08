package com.projectpal.controller;

import java.net.URI;
import java.util.List;

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

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.utils.ProjectUtil;

@RestController
@RequestMapping("/epic")
public class EpicController {
	@Autowired
	public EpicController(EpicRepository epicRepo) {
		this.epicRepo = epicRepo;
	}

	private final EpicRepository epicRepo;

	@GetMapping("/list")
	public ResponseEntity<List<Epic>> getEpicList() {
		
		Project project = ProjectUtil.getProjectNotNull();

		List<Epic> epics = epicRepo.findAllByProject(project)
				.orElseThrow(() -> new ResourceNotFoundException("no epics found"));

		epics.sort((epic1, epic2) -> Integer.compare(epic1.getPriority(), epic2.getPriority()));

		return ResponseEntity.ok(epics);
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createEpic(@RequestBody Epic epic) {

		if (epic == null || epic.getName() == null || epic.getPriority() == null)
			throw new BadRequestException("request body is null");

		epic.setProject(ProjectUtil.getProjectNotNull());

		epicRepo.save(epic);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/epic").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/description/{id}")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to update description of epics from other projects");
		
		epic.setDescription(description);
		
		epicRepo.save(epic);
		
		return ResponseEntity.status(204).build();
	}
	
	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/priority/{id}")
	@Transactional
	public ResponseEntity<Void> updatePriority(@RequestParam Byte priority, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to update priority of epics from other projects");

		if (priority < 0 || priority > 255)
			throw new BadRequestException("value is too large or too small");

		epic.setPriority(priority);

		epicRepo.save(epic);

		return ResponseEntity.status(204).build();

	}
	
	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		if(progress == null)
			throw new BadRequestException("request is null");
		
		epic.setProgress(progress);

		epicRepo.save(epic);

		return ResponseEntity.status(204).build();
	}
	
	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteEpic(@PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		epicRepo.delete(epic);

		return ResponseEntity.status(204).build();
	}
}
