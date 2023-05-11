package com.projectpal.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.repository.InvitationRepository;
import com.projectpal.repository.ProjectRepository;

@Service
public class ScheduledDeleteService {
	@Autowired
	public ScheduledDeleteService(AnnouncementRepository announcementRepo, InvitationRepository invitationRepo,
			ProjectRepository projectRepo) {

		this.announcementRepo = announcementRepo;
		this.invitationRepo = invitationRepo;
		this.projectRepo = projectRepo;
	}

	private final AnnouncementRepository announcementRepo;
	
	private final InvitationRepository invitationRepo;
	
	private final ProjectRepository projectRepo;
	
	 @Scheduled(cron = "0 0 0 * * *")
	    public void deleteExpiredInvitations() {
	        LocalDate oneWeekAgo = LocalDate.now().minusWeeks(1);
	        invitationRepo.deleteByIssueDateBefore(oneWeekAgo);
	    }
	 
	 @Scheduled(cron = "0 0 0 * * *")
	    public void deleteExpiredAnnouncements() {
	        LocalDate oneMonthAgo = LocalDate.now().minusWeeks(4);
	        announcementRepo.deleteByIssueDateBefore(oneMonthAgo);
	    }
	 
	 @Scheduled(cron = "0 0 0 1 * *")
	    public void deleteExpiredProjects() {
	        LocalDate threeMonthAgo = LocalDate.now().minusWeeks(12);
	        projectRepo.deleteByLastAccessedDateBefore(threeMonthAgo);
	    }
	 
}
