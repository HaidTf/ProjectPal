package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.Project;
import com.projectpal.service.admin.project.AdminProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/projects/{projectId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminProjectController {

	private final AdminProjectService projectService;

	@GetMapping()
	public ResponseEntity<Project> getProject(@PathVariable long projectId) {

		return ResponseEntity.ok(projectService.findProjectById(projectId));

	}

	@DeleteMapping()
	public ResponseEntity<Void> deleteProject(@PathVariable long projectId) {

		projectService.deleteProject(projectId);

		return ResponseEntity.status(204).build();
	}
}
