package com.projectpal.service.announcement;

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
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.security.context.AuthenticationContextFacade;
import com.projectpal.validation.PageValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

	private final AnnouncementRepository announcementRepo;

	private final AuthenticationContextFacade authenticationContextFacadeImpl;

	@Transactional(readOnly = true)
	@Override
	public Announcement findAnnouncementById(long announcementId) {
		return announcementRepo.findById(announcementId)
				.orElseThrow(() -> new EntityNotFoundException(Announcement.class));

	}

	@Transactional(readOnly = true)
	@Override
	public AnnouncementResponseDto findAnnouncementDtoByIdAndProject(long announcementId, Project project) {
		return announcementRepo.findAnnouncementDtoByIdAndProject(announcementId, project)
				.orElseThrow(() -> new EntityNotFoundException(Announcement.class));

	}

	@Transactional
	@Override
	public void createAnnouncement(User currentUser, Announcement announcement) {

		announcement.setAnnouncer(currentUser);

		announcement.setProject(currentUser.getProject());

		announcementRepo.save(announcement);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void deleteAnnouncement(long announcementId) {

		Announcement announcement = announcementRepo
				.findByIdAndProject(announcementId, authenticationContextFacadeImpl.getCurrentUser().getProject())
				.orElseThrow(() -> new EntityNotFoundException(Announcement.class));

		announcementRepo.delete(announcement);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<Announcement> findPageByProject(Project project, int page, int size) {

		PageValidator.validatePage(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return announcementRepo.findAllByProject(project, pageable);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<AnnouncementResponseDto> findAnnouncementDtoPageByProject(Project project, int page, int size) {

		PageValidator.validatePage(page, size);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("issueDate")));

		return announcementRepo.findAnnouncementDtoPageByProject(project, pageable);
	}
}
