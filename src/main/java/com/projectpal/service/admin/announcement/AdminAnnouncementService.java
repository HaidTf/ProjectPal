package com.projectpal.service.admin.announcement;

import com.projectpal.entity.Announcement;

public interface AdminAnnouncementService {

	Announcement findAnnouncementById(long announcementId);

	void deleteAnnouncement(long announcementId);

}
