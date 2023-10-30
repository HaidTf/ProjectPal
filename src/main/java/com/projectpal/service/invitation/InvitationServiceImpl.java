package com.projectpal.service.invitation;

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
import com.projectpal.exception.client.BadRequestException;
import com.projectpal.exception.client.ConflictException;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.exception.client.ResourceNotFoundException;
import com.projectpal.repository.InvitationRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.validation.PageValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

	private final InvitationRepository invitationRepo;

	private final UserRepository userRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public Invitation findInvitationById(long invitationId) {

		return invitationRepo.findById(invitationId).orElseThrow(() -> new EntityNotFoundException(Invitation.class));

	}

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public SentInvitationResponseDto findSentInvitationDtoByIdAndProject(long invitationId, Project project) {

		return invitationRepo.findSentInvitationDtoByIdAndProject(invitationId, project)
				.orElseThrow(() -> new EntityNotFoundException(Invitation.class));

	}

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public ReceivedInvitationResponseDto findReceivedInvitationDtoByIdAndUser(long invitationId, User invitedUser) {

		return invitationRepo.findReceivedInvitationDtoByIdAndUser(invitationId, invitedUser)
				.orElseThrow(() -> new EntityNotFoundException(Invitation.class));

	}

	@Transactional
	@Override
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

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public Page<SentInvitationResponseDto> findSentInvitationDtoPageByProject(Project project, int page, int size) {

		PageValidator.validatePage(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return invitationRepo.findSentInvitationDtoPageByProject(project, pageable);

	}

	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	@Override
	public List<ReceivedInvitationResponseDto> findReceivedInvitationDtoListByUser(User user) {

		return invitationRepo.findReceivedInvitationDtoListByInvitedUser(user, Sort.by(Sort.Order.desc("issueDate")));

	}

	@Transactional
	@Override
	public void userAcceptsInvitation(User user, long invitationId) {

		Invitation invitation = invitationRepo.findByIdAndInvitedUser(invitationId, user)
				.orElseThrow(() -> new EntityNotFoundException(Invitation.class));

		user.setProject(invitation.getProject());

		user.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		userRepo.save(user);

		invitationRepo.delete(invitation);

	}

	@Transactional
	@Override
	public void rejectInvitation(long invitationId) {

		Invitation invitation = invitationRepo
				.findByIdAndInvitedUser(invitationId, authenticationContextFacadeImpl.getCurrentUser())
				.orElseThrow(() -> new EntityNotFoundException(Invitation.class));

		invitationRepo.delete(invitation);
	}

}
