package com.projectpal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.dto.response.entity.SentInvitationResponseDto;
import com.projectpal.dto.response.entity.ReceivedInvitationResponseDto;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.InvitationRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.validation.PageValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationService {

	private final InvitationRepository invitationRepo;

	private final UserRepository userRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true)
	public Invitation findInvitationById(long invitationId) {

		return invitationRepo.findById(invitationId)
				.orElseThrow(() -> new ResourceNotFoundException("Invitation does not exist"));

	}

	@Transactional(readOnly = true)
	public SentInvitationResponseDto findSentInvitationDtoByIdAndProject(long invitationId, Project project) {

		return invitationRepo.findSentInvitationDtoByIdAndProject(invitationId, project)
				.orElseThrow(() -> new ResourceNotFoundException("Invitation does not exist"));

	}

	@Transactional(readOnly = true)
	public ReceivedInvitationResponseDto findReceivedInvitationDtoByIdAndUser(long invitationId, User invitedUser) {

		return invitationRepo.findReceivedInvitationDtoByIdAndUser(invitationId, invitedUser)
				.orElseThrow(() -> new ResourceNotFoundException("Invitation does not exist"));

	}

	@Transactional
	public Invitation inviteUserToProject(long userId, Project project) {

		User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (authenticationContextFacadeImpl.getCurrentUser().getId() == userId)
			throw new BadRequestException("you can not send an invitation to yourself");

		if (user.getProject().getId() == project.getId())
			throw new ConflictException("invited user is already in the intended project");

		Invitation invitation = new Invitation(user, project);

		invitationRepo.save(invitation);

		return invitation;
	}

	@Transactional(readOnly = true)
	public Page<SentInvitationResponseDto> findSentInvitationDtoPageByProject(Project project, int page,
			int size) {

		PageValidator.validatePage(page, size);
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return invitationRepo.findSentInvitationDtoPageByProject(project, pageable);

	}

	@Transactional(readOnly = true)
	public List<ReceivedInvitationResponseDto> findReceivedInvitationDtoListByUser(User user) {

		return invitationRepo.findReceivedInvitationDtoListByInvitedUser(user, Sort.by(Sort.Order.desc("issueDate")));

	}

	@Transactional
	public void userAcceptsInvitation(User user, long invitationId) {

		Invitation invitation = invitationRepo.findByIdAndInvitedUser(invitationId, user)
				.orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

		user.setProject(invitation.getProject());

		user.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		userRepo.save(user);

		invitationRepo.delete(invitation);

	}

	@Transactional
	public void rejectInvitation(long invitationId) {

		Invitation invitation = invitationRepo
				.findByIdAndInvitedUser(invitationId, authenticationContextFacadeImpl.getCurrentUser())
				.orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

		invitationRepo.delete(invitation);
	}

}
