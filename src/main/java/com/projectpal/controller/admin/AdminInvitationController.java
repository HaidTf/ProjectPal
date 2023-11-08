package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.Invitation;
import com.projectpal.service.admin.invitation.AdminInvitationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/invitations/{invitationId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminInvitationController {

	private final AdminInvitationService invitationService;

	@GetMapping
	public ResponseEntity<Invitation> getInvitation(@PathVariable long invitationId) {

		return ResponseEntity.ok(invitationService.findInvitationById(invitationId));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteInvitation(@PathVariable long invitationId) {

		invitationService.deleteInvitation(invitationId);

		return ResponseEntity.status(204).build();
	}
}
