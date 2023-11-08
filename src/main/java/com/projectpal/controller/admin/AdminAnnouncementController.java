package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.Announcement;
import com.projectpal.service.admin.announcement.AdminAnnouncementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/announcements/{announcementId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminAnnouncementController {

	private final AdminAnnouncementService announcementService;

	@GetMapping
	public ResponseEntity<Announcement> getAnnouncement(@PathVariable long announcementId) {

		return ResponseEntity.ok(announcementService.findAnnouncementById(announcementId));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteAnnouncement(@PathVariable long announcementId) {

		announcementService.deleteAnnouncement(announcementId);

		return ResponseEntity.status(204).build();
	}

}
