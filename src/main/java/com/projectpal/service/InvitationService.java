package com.projectpal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
import com.projectpal.utils.UserEntityAccessValidationUtil;

@Service
public class InvitationService {

	@Autowired
	public InvitationService(InvitationRepository invitationRepo, UserRepository userRepo, UserService userService,
			AuthenticationContextFacade authenticationContextFacadeImpl) {
		this.invitationRepo = invitationRepo;
		this.userRepo = userRepo;
		this.userService = userService;
		this.authenticationContextFacadeImpl = authenticationContextFacadeImpl;
	}

	private final InvitationRepository invitationRepo;

	private final UserRepository userRepo;

	private final UserService userService;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	public Invitation findInvitationById(long invitationId) {

		return invitationRepo.findById(invitationId)
				.orElseThrow(() -> new ResourceNotFoundException("Invitation does not exist"));

	}

	public Invitation inviteUserToProject(long userId, Project project) {

		User user = userService.findUserById(userId);

		if (authenticationContextFacadeImpl.getCurrentUser().getId() == userId)
			throw new BadRequestException("you can not send an invitation to yourself");

		if (user.getProject().getId() == project.getId())
			throw new ConflictException("invited user is already in the intended project");

		Invitation invitation = new Invitation(user, project);

		invitationRepo.save(invitation);

		return invitation;
	}

	public Page<Invitation> findPageByProject(Project project, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return invitationRepo.findAllByProject(project, pageable);

	}

	public List<Invitation> findAllByUser(User user) {

		return invitationRepo.findAllByInvitedUser(user, Sort.by(Sort.Order.desc("issueDate")));

	}

	public void userAcceptsInvitation(User user, long invitationId) {

		Invitation invitation = this.findInvitationById(invitationId);

		UserEntityAccessValidationUtil.verifyUserAccessToUserInvitation(user, invitation);

		user.setProject(invitation.getProject());

		user.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		userRepo.save(user);

		invitationRepo.delete(invitation);

	}

	public void rejectInvitation(long invitationId) {

		Invitation invitation = this.findInvitationById(invitationId);

		UserEntityAccessValidationUtil
				.verifyUserAccessToUserInvitation(authenticationContextFacadeImpl.getCurrentUser(), invitation);

		invitationRepo.delete(invitation);
	}

}
