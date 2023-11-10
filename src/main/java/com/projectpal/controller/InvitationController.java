package com.projectpal.controller;

import java.net.URI;
import java.util.List;

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
import com.projectpal.dto.response.entity.SentInvitationResponseDto;
import com.projectpal.dto.response.entity.ReceivedInvitationResponseDto;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.User;
import com.projectpal.service.invitation.InvitationService;
import com.projectpal.validation.ProjectMembershipValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InvitationController {

	private final InvitationService invitationService;

	@GetMapping("/project/invitations/{invitationId}")
	public ResponseEntity<SentInvitationResponseDto> getProjectRelatedInvitation(
			@AuthenticationPrincipal User currentUser, @PathVariable long invitationId) {

		log.debug("API:GET/api/project/invitations/{} invoked", invitationId);

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		SentInvitationResponseDto invitationDto = invitationService.findSentInvitationDtoByIdAndProject(invitationId,
				currentUser.getProject());

		return ResponseEntity.ok(invitationDto);

	}

	@GetMapping("/project/invitations")
	public ResponseEntity<CustomPageResponse<SentInvitationResponseDto>> getProjectRelatedInvitations(
			@AuthenticationPrincipal User currentUser, @RequestParam(required = false, defaultValue = "0") int page,
			@RequestParam(required = false, defaultValue = "10") int size) {

		log.debug("API:GET/api/project/invitations invoked");

		ProjectMembershipValidator.verifyUserProjectMembership(currentUser);

		Page<SentInvitationResponseDto> invitations = invitationService
				.findSentInvitationDtoPageByProject(currentUser.getProject(), page, size);

		return ResponseEntity.ok(new CustomPageResponse<SentInvitationResponseDto>(invitations));
	}

	@GetMapping("/users/me/invitations/{invitationId}")
	public ResponseEntity<ReceivedInvitationResponseDto> getReceivedInvitation(
			@AuthenticationPrincipal User currentUser, @PathVariable long invitationId) {

		log.debug("API:GET/api/users/me/invitations/{} invoked", invitationId);

		ReceivedInvitationResponseDto invitationDto = invitationService
				.findReceivedInvitationDtoByIdAndUser(invitationId, currentUser);

		return ResponseEntity.ok(invitationDto);

	}

	@GetMapping("/users/me/invitations")
	public ResponseEntity<ListHolderResponse<ReceivedInvitationResponseDto>> getReceivedInvitations(
			@AuthenticationPrincipal User currentUser) {

		log.debug("API:GET/api/users/me/invitations invoked");

		List<ReceivedInvitationResponseDto> invitations = invitationService
				.findReceivedInvitationDtoListByUser(currentUser);

		return ResponseEntity.ok(new ListHolderResponse<ReceivedInvitationResponseDto>(invitations));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/users/{userId}/invitations")
	public ResponseEntity<Invitation> invite(@AuthenticationPrincipal User currentUser, @PathVariable long userId) {

		log.debug("API:POST/api/users/{}/invitations invoked", userId);
		
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

		log.debug("API:PATCH/api/users/me/invitations/{} invoked", invitationId);
		
		invitationService.userAcceptsInvitation(currentUser, invitationId);

		return ResponseEntity.status(204).build();

	}

	@DeleteMapping("/users/me/invitations/{invitationId}")
	public ResponseEntity<Void> rejectInvitation(@PathVariable long invitationId) {

		log.debug("API:DELETE/api/users/me/invitations/{} invoked", invitationId);
		
		invitationService.rejectInvitation(invitationId);

		return ResponseEntity.status(204).build();

	}

}
