package com.projectpal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.response.CustomPageResponse;
import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.service.InvitationService;
import com.projectpal.service.UserService;
import com.projectpal.utils.SecurityContextUtil;

@RestController
public class InvitationController {

	@Autowired
	public InvitationController(InvitationService invitationService, UserService userService) {
		this.invitationService = invitationService;
		this.userService = userService;
	}

	private final InvitationService invitationService;

	private final UserService userService;

	@GetMapping("/project/invitations/{invitationId}")
	public ResponseEntity<Invitation> getProjectRelatedInvitation(@PathVariable long invitationId) {

		Project project = SecurityContextUtil.getUserProjectNotNull();

		Invitation invitation = invitationService.findInvitationById(invitationId);

		if (invitation.getProject().getId() != project.getId())
			throw new ForbiddenException("You are not allowed access to other projects");

		return ResponseEntity.ok(invitation);

	}

	@GetMapping("/project/invitations")
	public ResponseEntity<CustomPageResponse<Invitation>> getProjectRelatedInvitations(
			@RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {

		Project project = SecurityContextUtil.getUserProjectNotNull();

		Page<Invitation> invitations = invitationService.findPageByProject(project, page, size);

		return ResponseEntity.ok(new CustomPageResponse<Invitation>(invitations));
	}

	@GetMapping("/users/me/invitations/{invitationId}")
	public ResponseEntity<Invitation> getReceivedInvitation(@PathVariable long invitationId) {

		User user = SecurityContextUtil.getUser();

		Invitation invitation = invitationService.findInvitationById(invitationId);

		if (invitation.getInvitedUser().getId() != user.getId())
			throw new ForbiddenException("You are not allowed access to another user's information");

		return ResponseEntity.ok(invitation);

	}

	@GetMapping("/users/me/invitations")
	public ResponseEntity<ListHolderResponse<Invitation>> getReceivedInvitations() {

		User user = SecurityContextUtil.getUser();

		List<Invitation> invitations = invitationService.findAllByUser(user);

		return ResponseEntity.ok(new ListHolderResponse<Invitation>(invitations));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/users/{userId}/invitations")
	@Transactional
	public ResponseEntity<Invitation> invite(@PathVariable long userId) {

		User user = userService.findUserById(userId);

		if (SecurityContextUtil.getUser().getId() == userId)
			throw new BadRequestException("you can not send an invitation to yourself");

		Invitation invitation = invitationService.inviteUserToProject(user, SecurityContextUtil.getUserProject());

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/invitations/" + invitation.getId())
				.build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(invitation);
	}

	@PreAuthorize("!hasRole('USER_PROJECT_OWNER')")
	@PatchMapping("/users/me/invitations/{invitationId}")
	@Transactional
	public ResponseEntity<Void> acceptInvitation(@PathVariable long invitationId) {

		Invitation invitation = invitationService.findInvitationById(invitationId);

		User user = SecurityContextUtil.getUser();

		if (invitation.getInvitedUser().getId() != user.getId())
			throw new ForbiddenException("you are not allowed to modify other user's invitations");

		invitationService.userAcceptsInvitation(user, invitation);

		return ResponseEntity.status(204).build();

	}

	@DeleteMapping("/users/me/invitations/{invitationId}")
	@Transactional
	public ResponseEntity<Void> rejectInvitation(@PathVariable long invitationId) {

		Invitation invitation = invitationService.findInvitationById(invitationId);

		if (invitation.getInvitedUser().getId() != SecurityContextUtil.getUser().getId())
			throw new ForbiddenException("you are not allowed to modify other user's invitations");

		invitationService.rejectInvitation(invitation);

		return ResponseEntity.status(204).build();

	}

}
