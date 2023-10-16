package com.projectpal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.projectpal.dto.response.entity.InvitationResponseDto;
import com.projectpal.entity.Invitation;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

	@Mapping(source = "invitedUser.id", target = "invitedUserId")
	@Mapping(source = "project.id", target = "projectId")
	@Mapping(source = "project.name", target = "projectName")
	InvitationResponseDto toDto(Invitation invitation);

}
