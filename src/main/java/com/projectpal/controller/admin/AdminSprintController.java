package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.Sprint;
import com.projectpal.service.admin.sprint.AdminSprintService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/sprints/{sprintId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminSprintController {

	private final AdminSprintService sprintService;

	@GetMapping
	public ResponseEntity<Sprint> getSprint(@PathVariable long sprintId) {

		return ResponseEntity.ok(sprintService.findSprintById(sprintId));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteSprint(@PathVariable long sprintId) {

		sprintService.deleteSprint(sprintId);

		return ResponseEntity.status(204).build();
	}

}
