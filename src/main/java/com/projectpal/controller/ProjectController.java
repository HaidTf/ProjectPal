package com.projectpal.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.exception.BadRequestException;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	public ProjectController(ProjectRepository projectRepo, UserRepository userRepo) {

		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
	}

	private final ProjectRepository projectRepo;

	private final UserRepository userRepo;

	@GetMapping("")
	public ResponseEntity<Project> getProject() {

		Project project = ProjectUtil.getProjectNotNull();

		return ResponseEntity.ok(project);
	}

	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createProject(@RequestBody Project project) {

		if (project.getName() == null)
			throw new BadRequestException("");

		User user = SecurityContextUtil.getUser();

		user.setProject(project);
		project.setOwner(user);

		projectRepo.save(project);
		userRepo.save(user);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PatchMapping("/description")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestParam String description) {

		ProjectUtil.onlyProjectOwnerAllowed();

		Project project = ProjectUtil.getProjectNotNull();
		
		project.setDescription(description);

		projectRepo.save(project);

		return ResponseEntity.status(204).build();

	}

	@DeleteMapping("/delete")
	@Transactional
	public ResponseEntity<Void> deleteProject() {

		Project project = ProjectUtil.getProjectNotNull();
			
		ProjectUtil.onlyProjectOwnerAllowed();

		projectRepo.delete(project);
		
		return ResponseEntity.status(204).build();

	}

}
