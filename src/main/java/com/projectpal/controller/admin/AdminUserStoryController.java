package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.UserStory;
import com.projectpal.service.admin.userstory.AdminUserStoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/userStories/{userStoryId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminUserStoryController {

	private final AdminUserStoryService userStoryService;

	@GetMapping
	public ResponseEntity<UserStory> getUserStory(@PathVariable long userStoryId) {

		return ResponseEntity.ok(userStoryService.findUserStoryById(userStoryId));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteUserStory(@PathVariable long userStoryId) {

		userStoryService.deleteUserStory(userStoryId);

		return ResponseEntity.status(204).build();
	}

}
