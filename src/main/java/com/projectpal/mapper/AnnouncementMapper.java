package com.projectpal.mapper;

import org.mapstruct.Mapper;

import com.projectpal.dto.request.entity.AnnouncementCreationDto;
import com.projectpal.dto.response.entity.AnnouncementResponseDto;
import com.projectpal.entity.Announcement;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {

	AnnouncementResponseDto toDto(Announcement announcement);

	Announcement toAnnouncement(AnnouncementCreationDto announcementCreationDto);

}
