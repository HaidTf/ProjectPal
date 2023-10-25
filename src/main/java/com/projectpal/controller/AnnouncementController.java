package com.projectpal.controller;

import java.net.URI;

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

import com.projectpal.dto.mapper.AnnouncementMapper;
import com.projectpal.dto.request.entity.AnnouncementCreationDto;
import com.projectpal.dto.response.CustomPageResponse;
import com.projectpal.dto.response.entity.AnnouncementResponseDto;
import com.projectpal.entity.Announcement;
import com.projectpal.entity.User;
import com.projectpal.service.AnnouncementService;
import com.projectpal.validation.ProjectMembershipValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/project/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

	private final AnnouncementService announcementService;

	private final AnnouncementMapper announcementMapper;

	@GetMapping("/{announcementId}")
	public ResponseEntity<AnnouncementResponseDto> getAnnouncement(@AuthenticationPrincipal User currentUser,
			@PathVariable long announcementId) {

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		AnnouncementResponseDto announcementDto = announcementService.findAnnouncementDtoByIdAndProject(announcementId,
				currentUser.getProject());

		return ResponseEntity.ok(announcementDto);

	}

	@GetMapping("")
	public ResponseEntity<CustomPageResponse<AnnouncementResponseDto>> getAnnouncements(
			@AuthenticationPrincipal User currentUser, @RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "5") int size) {

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		Page<AnnouncementResponseDto> announcements = announcementService
				.findAnnouncementDtoPageByProject(currentUser.getProject(), page, size);

		return ResponseEntity.ok(new CustomPageResponse<AnnouncementResponseDto>(announcements));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("")
	public ResponseEntity<Announcement> createAnnouncement(@AuthenticationPrincipal User currentUser,
			@Valid @RequestBody AnnouncementCreationDto announcementCreationDto) {

		Announcement announcement = announcementMapper.toAnnouncement(announcementCreationDto);

		announcementService.createAnnouncement(currentUser, announcement);

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
