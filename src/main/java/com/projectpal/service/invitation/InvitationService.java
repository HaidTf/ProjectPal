package com.projectpal.service.invitation;

import java.util.List;

import org.springframework.data.domain.Page;

import com.projectpal.dto.response.entity.ReceivedInvitationResponseDto;
import com.projectpal.dto.response.entity.SentInvitationResponseDto;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;

public interface InvitationService {

	public Invitation findInvitationById(long invitationId);

	public SentInvitationResponseDto findSentInvitationDtoByIdAndProject(long invitationId, Project project);

	public ReceivedInvitationResponseDto findReceivedInvitationDtoByIdAndUser(long invitationId, User invitedUser);

	public Invitation inviteUserToProject(long userId, Project project);

	public Page<SentInvitationResponseDto> findSentInvitationDtoPageByProject(Project project, int page, int size);

	public List<ReceivedInvitationResponseDto> findReceivedInvitationDtoListByUser(User user);

	public void userAcceptsInvitation(User user, long invitationId);

	public void rejectInvitation(long invitationId);

}
