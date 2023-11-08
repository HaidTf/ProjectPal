package com.projectpal.service.admin.invitation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Invitation;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.InvitationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminInvitationServiceImpl implements AdminInvitationService {

	private final InvitationRepository invitationRepo;

	@Override
	@Transactional(readOnly = true)
	public Invitation findInvitationById(long invitationId) {
		return invitationRepo.findById(invitationId).orElseThrow(() -> new EntityNotFoundException(Invitation.class));
	}

	@Override
	@Transactional
	public void deleteInvitation(long invitationId) {
		invitationRepo.deleteById(invitationId);
	}
}
