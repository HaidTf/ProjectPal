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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.InvitationRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.utils.ProjectUtil;
import com.projectpal.utils.SecurityContextUtil;

import jakarta.persistence.EntityNotFoundException;

@RestController
public class InvitationController {

	@Autowired
	public InvitationController(InvitationRepository invitationRepo, UserRepository userRepo) {
		this.invitationRepo = invitationRepo;
		this.userRepo = userRepo;
	}

	private final InvitationRepository invitationRepo;

	private final UserRepository userRepo;

	@GetMapping("/projects/invitations/{invitationId}")
	public ResponseEntity<Invitation> getProjectRelatedInvitation(@PathVariable long invitationId) {

		Project project = ProjectUtil.getProjectNotNull();

		Invitation invitation = invitationRepo.getReferenceById(invitationId);

		try {
			if (invitation.getProject().getId() != project.getId())
				throw new ForbiddenException("You are not allowed access to other projects");
		} catch (EntityNotFoundException ex) {
			throw new ResourceNotFoundException("Invitation does not exist");
		}

		return ResponseEntity.ok(invitation);

	}

	@GetMapping("/projects/invitations")
	public ResponseEntity<ListHolderResponse<Invitation>> getProjectRelatedInvitations() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Invitation> invitations = invitationRepo.findAllByProject(project).orElse(new ArrayList<Invitation>(0));

		invitations.sort((inv1, inv2) -> inv1.getIssueDate().compareTo(inv2.getIssueDate()));

		return ResponseEntity.ok(new ListHolderResponse<Invitation>(invitations));
	}

	@GetMapping("/users/invitations/{invitationId}")
	public ResponseEntity<Invitation> getReceivedInvitation(@PathVariable long invitationId) {

		User user = SecurityContextUtil.getUser();

		Invitation invitation = invitationRepo.getReferenceById(invitationId);

		try {
			if (invitation.getInvitedUser().getId() != user.getId())
				throw new ForbiddenException("You are not allowed access to other users information");
		} catch (EntityNotFoundException ex) {
			throw new ResourceNotFoundException("Invitation does not exist");
		}

		return ResponseEntity.ok(invitation);

	}

	@GetMapping("/users/invitations")
	public ResponseEntity<ListHolderResponse<Invitation>> getReceivedInvitations() {

		User user = SecurityContextUtil.getUser();

		List<Invitation> invitations = invitationRepo.findAllByInvitedUser(user).orElse(new ArrayList<Invitation>(0));

		invitations.sort((inv1, inv2) -> inv1.getIssueDate().compareTo(inv2.getIssueDate()));

		return ResponseEntity.ok(new ListHolderResponse<Invitation>(invitations));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/users/{name}/invitations")
	@Transactional
	public ResponseEntity<Invitation> invite(@PathVariable String name) {

		User user = userRepo.findUserByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("the user you want to invite is not found"));

		if (user.getProject().getId() == ProjectUtil.getProjectNotNull().getId())
			throw new BadRequestException("invited user is already in the intended project");

		if (SecurityContextUtil.getUser().getName() == name)
			throw new BadRequestException("you cant send an invitation to yourself");

		Invitation invitation = new Invitation(user, ProjectUtil.getProjectNotNull());

		invitationRepo.save(invitation);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/projects/invitations/" + invitation.getId())
				.build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(invitation);
	}

	@PreAuthorize("!hasRole('USER_PROJECT_OWNER')")
	@PatchMapping("/users/invitations/{invitationId}")
	@Transactional
	public ResponseEntity<Void> acceptInvitation(@PathVariable long invitationId) {

		Invitation invitation = invitationRepo.findById(invitationId)
				.orElseThrow(() -> new ResourceNotFoundException("invitation not found"));

		if (invitation.getInvitedUser().getId() != SecurityContextUtil.getUser().getId())
			throw new ForbiddenException("you are not allowed to modify other user's invitations");

		User user = SecurityContextUtil.getUser();

		user.setProject(invitation.getProject());

		user.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		userRepo.save(user);

		invitationRepo.delete(invitation);

		return ResponseEntity.status(204).build();

	}

	@DeleteMapping("/users/invitations/{invitationId}")
	@Transactional
	public ResponseEntity<Void> rejectInvitation(@PathVariable long id) {

		Invitation invitation = invitationRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("invitation not found"));

		if (invitation.getInvitedUser().getId() != SecurityContextUtil.getUser().getId())
			throw new ForbiddenException("you are not allowed to modify other user's invitations");

		invitationRepo.delete(invitation);

		return ResponseEntity.status(204).build();

	}

}
