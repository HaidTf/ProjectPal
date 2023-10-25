package com.projectpal.dto.mapper;

import org.mapstruct.Mapper;

import com.projectpal.dto.request.entity.AnnouncementCreationDto;
import com.projectpal.entity.Announcement;

@Mapper(componentModel = "spring")
public interface AnnouncementMapper {

	Announcement toAnnouncement(AnnouncementCreationDto announcementCreationDto);

}
