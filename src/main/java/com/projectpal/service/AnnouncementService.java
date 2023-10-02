package com.projectpal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.ProjectUtil;

@Service
public class AnnouncementService {

	@Autowired
	public AnnouncementService(AnnouncementRepository announcementRepo) {
		this.announcementRepo = announcementRepo;
	}

	private final AnnouncementRepository announcementRepo;

	public Announcement findAnnouncementById(long announcementId) {
		return announcementRepo.findById(announcementId)
				.orElseThrow(() -> new ResourceNotFoundException("Announcement does not exist"));

	}

	public void createAnnouncement(Project project, Announcement announcement) {

		announcement.setProject(ProjectUtil.getProjectNotNull());

		announcementRepo.save(announcement);

	}

	public void deleteAnnouncement(Announcement announcement) {

		announcementRepo.delete(announcement);
	}

	public Page<Announcement> findPageByProject(Project project, int page, int size) {

		if (size > MaxAllowedUtil.MAX_PAGE_SIZE)
			throw new ConflictException("Page size exceeded size limit");

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return announcementRepo.findAllByProject(project, pageable);
	}

}
