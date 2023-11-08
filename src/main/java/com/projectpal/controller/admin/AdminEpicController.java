package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.Epic;
import com.projectpal.service.admin.epic.AdminEpicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/epics/{epicId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminEpicController {

	private final AdminEpicService epicService;

	@GetMapping
	public ResponseEntity<Epic> getEpic(@PathVariable long epicId) {

		return ResponseEntity.ok(epicService.findEpicById(epicId));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteEpic(@PathVariable long epicId) {

		epicService.deleteEpic(epicId);

		return ResponseEntity.status(204).build();
	}

}
