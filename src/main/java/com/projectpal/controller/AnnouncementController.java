package com.projectpal.controller;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.utils.ProjectUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects/announcements")
public class AnnouncementController {

	@Autowired
	public AnnouncementController(AnnouncementRepository announcementRepo) {
		this.announcementRepo = announcementRepo;
	}

	private final AnnouncementRepository announcementRepo;

	@GetMapping("/{announcementId}")
	public ResponseEntity<Announcement> getAnnouncement(@PathVariable long announcementId) {

		Project project = ProjectUtil.getProjectNotNull();

		Announcement announcement = announcementRepo.getReferenceById(announcementId);

		try {
			if (announcement.getProject().getId() != project.getId())
				throw new ForbiddenException("You are not allowed access to other projects");
		} catch (EntityNotFoundException ex) {
			throw new ResourceNotFoundException("Announcement does not exist");
		}

		return ResponseEntity.ok(announcement);

	}

	@GetMapping("")
	public ResponseEntity<ListHolderResponse<Announcement>> getAnnouncements() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Announcement> announcements = announcementRepo.findAllByProject(project)
				.orElse(new ArrayList<Announcement>(0));

		announcements.sort(
				(announcement1, announcement2) -> announcement1.getIssueDate().compareTo(announcement2.getIssueDate()));

		return ResponseEntity.ok(new ListHolderResponse<Announcement>(announcements));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody Announcement announcement) {

		announcement.setProject(ProjectUtil.getProjectNotNull());

		announcementRepo.save(announcement);

		UriComponents uriComponents = UriComponentsBuilder
				.fromPath("/api/projects/announcements/" + announcement.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(announcement);

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/{AnnouncementId}")
	@Transactional
	public ResponseEntity<Void> deleteAnnouncement(@PathVariable long announcementId) {

		Announcement announcement = announcementRepo.findById(announcementId)
				.orElseThrow(() -> new ResourceNotFoundException("announcement not found"));

		if (announcement.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		announcementRepo.delete(announcement);

		return ResponseEntity.status(204).build();
	}
}
