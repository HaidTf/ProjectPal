package com.projectpal.service.integration;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import com.projectpal.entity.Project;
import com.projectpal.entity.Announcement;
import com.projectpal.entity.Invitation;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.repository.InvitationRepository;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.service.ScheduledDeleteService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ScheduledDeleteServiceTest {

	@Autowired
	public ScheduledDeleteServiceTest(ScheduledDeleteService service, ProjectRepository projectRepo,
			AnnouncementRepository announcementRepo, InvitationRepository invitationRepo) {
		this.scheduledDeleteService = service;
		this.projectRepo = projectRepo;
		this.announcementRepo = announcementRepo;
		this.invitationRepo = invitationRepo;
	}

	private final ScheduledDeleteService scheduledDeleteService;

	private final ProjectRepository projectRepo;

	private final AnnouncementRepository announcementRepo;

	private final InvitationRepository invitationRepo;

	@Test
	public void testDeleteExpiredProjects() {
		
		Project expiredProject = new Project("projcet", "description");
		//Last accessed Date more than 3 months ago
		expiredProject.setLastAccessedDate(LocalDate.now().minusMonths(4));
		projectRepo.save(expiredProject);

		scheduledDeleteService.deleteExpiredProjects();
		
		Project mustBeDeletedProject = projectRepo.findById(expiredProject.getId()).orElse(null);
		
		assertNull(mustBeDeletedProject);
	}

	@Test
	public void testDeleteExpiredAnnouncements() {

		Announcement expiredAnnouncement= new Announcement("title","description");
		//Last accessed Date more than 4 weeks ago
		expiredAnnouncement.setIssueDate(LocalDate.now().minusWeeks(5));
		announcementRepo.save(expiredAnnouncement);
		
		scheduledDeleteService.deleteExpiredAnnouncements();
		
		Announcement mustBeDeletedAnnouncement = announcementRepo.findById(expiredAnnouncement.getId()).orElse(null);
		
		assertNull(mustBeDeletedAnnouncement);
		
	}

	@Test
	public void testDeleteExpiredInvitations() {
		
		Invitation expiredInvitation= new Invitation();
		//Last accessed Date more than one week ago
		expiredInvitation.setIssueDate(LocalDate.now().minusWeeks(2));
		invitationRepo.save(expiredInvitation);
		
		scheduledDeleteService.deleteExpiredInvitations();
		
		Invitation mustBeDeletedinvitation = invitationRepo.findById(expiredInvitation.getId()).orElse(null);
		
		assertNull(mustBeDeletedinvitation);
	}
}
