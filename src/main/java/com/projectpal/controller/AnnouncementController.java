package com.projectpal.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.response.CustomPageResponse;
import com.projectpal.entity.Announcement;
import com.projectpal.entity.User;
import com.projectpal.service.AnnouncementService;
import com.projectpal.utils.ProjectMembershipValidationUtil;
import com.projectpal.utils.UserEntityAccessValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/project/announcements")
public class AnnouncementController {

	@Autowired
	public AnnouncementController(AnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	private final AnnouncementService announcementService;

	@GetMapping("/{announcementId}")
	public ResponseEntity<Announcement> getAnnouncement(@AuthenticationPrincipal User currentUser,
			@PathVariable long announcementId) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		Announcement announcement = announcementService.findAnnouncementById(announcementId);

		UserEntityAccessValidationUtil.verifyUserAccessToAnnouncement(currentUser, announcement);

		return ResponseEntity.ok(announcement);

	}

	@GetMapping("")
	public ResponseEntity<CustomPageResponse<Announcement>> getAnnouncements(@AuthenticationPrincipal User currentUser,
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "5") int size) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		Page<Announcement> announcements = announcementService.findPageByProject(currentUser.getProject(), page, size);

		return ResponseEntity.ok(new CustomPageResponse<Announcement>(announcements));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Announcement> createAnnouncement(@AuthenticationPrincipal User currentUser,
			@Valid @RequestBody Announcement announcement) {

		announcementService.createAnnouncement(currentUser.getProject(), announcement);

		UriComponents uriComponents = UriComponentsBuilder
				.fromPath("/api/project/announcements/" + announcement.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(announcement);

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{AnnouncementId}")
	public ResponseEntity<Void> deleteAnnouncement(@PathVariable long announcementId) {

		announcementService.deleteAnnouncement(announcementId);

		return ResponseEntity.status(204).build();
	}
}
