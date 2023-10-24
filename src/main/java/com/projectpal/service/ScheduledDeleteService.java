package com.projectpal.service;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.repository.InvitationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduledDeleteService {

	private final AnnouncementRepository announcementRepo;

	private final InvitationRepository invitationRepo;

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void deleteExpiredInvitations() {
		LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
		invitationRepo.deleteByIssueDateBefore(oneWeekAgo);
	}

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void deleteExpiredAnnouncements() {
		LocalDate oneMonthAgo = LocalDate.now().minusWeeks(4);
		announcementRepo.deleteByIssueDateBefore(oneMonthAgo);
	}

}
