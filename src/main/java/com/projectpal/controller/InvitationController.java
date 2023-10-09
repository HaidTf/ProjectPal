package com.projectpal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.projectpal.entity.User;
import com.projectpal.service.InvitationService;
import com.projectpal.utils.ProjectMembershipValidationUtil;
import com.projectpal.utils.UserEntityAccessValidationUtil;

@RestController
public class InvitationController {

	@Autowired
	public InvitationController(InvitationService invitationService) {
		this.invitationService = invitationService;
	}

	private final InvitationService invitationService;

	@GetMapping("/project/invitations/{invitationId}")
	public ResponseEntity<Invitation> getProjectRelatedInvitation(@AuthenticationPrincipal User currentUser,
			@PathVariable long invitationId) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		Invitation invitation = invitationService.findInvitationById(invitationId);

		UserEntityAccessValidationUtil.verifyUserAccessToProjectInvitation(currentUser, invitation);

		return ResponseEntity.ok(invitation);

	}

	@GetMapping("/project/invitations")
	public ResponseEntity<CustomPageResponse<Invitation>> getProjectRelatedInvitations(
			@AuthenticationPrincipal User currentUser, @RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {

		ProjectMembershipValidationUtil.verifyUserProjectMembership(currentUser);

		Page<Invitation> invitations = invitationService.findPageByProject(currentUser.getProject(), page, size);

		return ResponseEntity.ok(new CustomPageResponse<Invitation>(invitations));
	}

	@GetMapping("/users/me/invitations/{invitationId}")
	public ResponseEntity<Invitation> getReceivedInvitation(@AuthenticationPrincipal User currentUser,
			@PathVariable long invitationId) {

		Invitation invitation = invitationService.findInvitationById(invitationId);

		UserEntityAccessValidationUtil.verifyUserAccessToUserInvitation(currentUser, invitation);

		return ResponseEntity.ok(invitation);

	}

	@GetMapping("/users/me/invitations")
	public ResponseEntity<ListHolderResponse<Invitation>> getReceivedInvitations(
			@AuthenticationPrincipal User currentUser) {

		List<Invitation> invitations = invitationService.findAllByUser(currentUser);

		return ResponseEntity.ok(new ListHolderResponse<Invitation>(invitations));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/users/{userId}/invitations")
	public ResponseEntity<Invitation> invite(@AuthenticationPrincipal User currentUser, @PathVariable long userId) {

		Invitation invitation = invitationService.inviteUserToProject(userId, currentUser.getProject());

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/project/invitations/" + invitation.getId())
				.build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(invitation);
	}

	@PreAuthorize("!hasRole('USER_PROJECT_OWNER')")
	@PatchMapping("/users/me/invitations/{invitationId}")
	public ResponseEntity<Void> acceptInvitation(@AuthenticationPrincipal User currentUser,
			@PathVariable long invitationId) {

		invitationService.userAcceptsInvitation(currentUser, invitationId);

		return ResponseEntity.status(204).build();

	}

	@DeleteMapping("/users/me/invitations/{invitationId}")
	public ResponseEntity<Void> rejectInvitation(@PathVariable long invitationId) {

		invitationService.rejectInvitation(invitationId);

		return ResponseEntity.status(204).build();

	}

}
