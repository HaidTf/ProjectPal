package com.projectpal.service.announcement;

import org.springframework.data.domain.Page;

import com.projectpal.dto.response.entity.AnnouncementResponseDto;
import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;

public interface AnnouncementService {

	public Announcement findAnnouncementById(long announcementId);

	public AnnouncementResponseDto findAnnouncementDtoByIdAndProject(long announcementId, Project project);

	public Page<Announcement> findPageByProject(Project project, int page, int size);

	public Page<AnnouncementResponseDto> findAnnouncementDtoPageByProject(Project project, int page, int size);

	public void createAnnouncement(User currentUser, Announcement announcement);

	public void deleteAnnouncement(long announcementId);

}
