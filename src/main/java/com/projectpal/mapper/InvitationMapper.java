package com.projectpal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.response.entity.ProjectInvitationResponseDto;
import com.projectpal.dto.response.entity.UserInvitationResponseDto;
import com.projectpal.entity.Invitation;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

	@Mapping(source = "invitedUser.id", target = "invitedUserId")
	@Mapping(source = "project.id", target = "projectId")
	@Mapping(source = "project.name", target = "projectName")
	UserInvitationResponseDto toUserInvitationDto(Invitation invitation);

	@Mapping(source = "invitedUser.id", target = "invitedUserId")
	@Mapping(source = "invitedUser.name", target = "invitedUserId")
	ProjectInvitationResponseDto toProjectInvitationDto(Invitation invitation);
	
}
