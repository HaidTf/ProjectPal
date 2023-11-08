package com.projectpal.service.admin.announcement;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Announcement;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.AnnouncementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

	private final AnnouncementRepository announcementRepo;

	@Override
	@Transactional(readOnly = true)
	public Announcement findAnnouncementById(long announcementId) {
		return announcementRepo.findById(announcementId)
				.orElseThrow(() -> new EntityNotFoundException(Announcement.class));
	}

	@Override
	@Transactional
	public void deleteAnnouncement(long announcementId) {
		announcementRepo.deleteById(announcementId);
	}
}
