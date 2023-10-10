package com.projectpal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.utils.MaxAllowedUtil;
import com.projectpal.utils.UserEntityAccessValidationUtil;

@Service
public class AnnouncementService {

	@Autowired
	public AnnouncementService(AnnouncementRepository announcementRepo,
			AuthenticationContextFacade authenticationContextFacadeImpl) {
		this.announcementRepo = announcementRepo;
		this.authenticationContextFacadeImpl = authenticationContextFacadeImpl;
	}

	private final AnnouncementRepository announcementRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true)
	public Announcement findAnnouncementById(long announcementId) {
		return announcementRepo.findById(announcementId)
				.orElseThrow(() -> new ResourceNotFoundException("Announcement does not exist"));

	}

	@Transactional
	public void createAnnouncement(Project project, Announcement announcement) {

		announcement.setProject(project);

		announcementRepo.save(announcement);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void deleteAnnouncement(long announcementId) {

		Announcement announcement = this.findAnnouncementById(announcementId);

		UserEntityAccessValidationUtil.verifyUserAccessToAnnouncement(authenticationContextFacadeImpl.getCurrentUser(),
				announcement);

		announcementRepo.delete(announcement);
	}

	@Transactional(readOnly = true)
	public Page<Announcement> findPageByProject(Project project, int page, int size) {

		if (size > MaxAllowedUtil.MAX_PAGE_SIZE)
			throw new ConflictException("Page size exceeded size limit");

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return announcementRepo.findAllByProject(project, pageable);
	}

}
