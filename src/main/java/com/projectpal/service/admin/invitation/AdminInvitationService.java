package com.projectpal.service.admin.invitation;

import com.projectpal.entity.Invitation;

public interface AdminInvitationService {

	Invitation findInvitationById(long invitationId);

	void deleteInvitation(long invitationId);

}
