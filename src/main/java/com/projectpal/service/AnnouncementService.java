package com.projectpal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.dto.response.entity.AnnouncementResponseDto;
import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.validation.PageValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

	private final AnnouncementRepository announcementRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true)
	public Announcement findAnnouncementById(long announcementId) {
		return announcementRepo.findById(announcementId)
				.orElseThrow(() -> new ResourceNotFoundException("Announcement does not exist"));

	}

	@Transactional(readOnly = true)
	public AnnouncementResponseDto findAnnouncementDtoByIdAndProject(long announcementId, Project project) {
		return announcementRepo.findAnnouncementDtoByIdAndProject(announcementId, project)
				.orElseThrow(() -> new ResourceNotFoundException("Announcement does not exist"));

	}

	@Transactional
	public void createAnnouncement(User currentUser, Announcement announcement) {

		announcement.setAnnouncer(currentUser);

		announcement.setProject(currentUser.getProject());

		announcementRepo.save(announcement);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void deleteAnnouncement(long announcementId) {

		Announcement announcement = announcementRepo
				.findByIdAndProject(announcementId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));

		announcementRepo.delete(announcement);
	}

	@Transactional(readOnly = true)
	public Page<Announcement> findPageByProject(Project project, int page, int size) {

		PageValidator.validatePage(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return announcementRepo.findAllByProject(project, pageable);
	}

	@Transactional(readOnly = true)
	public Page<AnnouncementResponseDto> findAnnouncementDtoPageByProject(Project project, int page, int size) {

		PageValidator.validatePage(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return announcementRepo.findAnnouncementDtoPageByProject(project, pageable);
	}
}
