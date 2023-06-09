package com.projectpal.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.Project;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.InvitationRepository;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.SprintRepository;
import com.projectpal.repository.TaskAttachmentRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.service.CacheService;
import com.projectpal.service.CacheServiceEpicAddOn;
import com.projectpal.service.CacheServiceProjectAddOn;
import com.projectpal.service.CacheServiceSprintAddOn;
import com.projectpal.service.CacheServiceUserStoryAddOn;

@RestController
@RequestMapping("/admin/entities/delete")
public class DeleteEntitiesAdminController {

	@Autowired
	public DeleteEntitiesAdminController(ProjectRepository projectRepo, EpicRepository epicRepo,
			SprintRepository sprintRepo, UserStoryRepository userStoryRepo, TaskRepository taskRepo,
			TaskAttachmentRepository taskAttachmentRepo, InvitationRepository invitationRepo,
			AnnouncementRepository announcementRepo, CacheServiceEpicAddOn cacheServiceEpicAddOn,
			CacheServiceSprintAddOn cacheServiceSprintAddOn, CacheService cacheService,
			CacheServiceProjectAddOn cacheServiceProjectAddOn) {
		this.projectRepo = projectRepo;
		this.epicRepo = epicRepo;
		this.sprintRepo = sprintRepo;
		this.userStoryRepo = userStoryRepo;
		this.taskRepo = taskRepo;
		this.taskAttachmentRepo = taskAttachmentRepo;
		this.invitationRepo = invitationRepo;
		this.announcementRepo = announcementRepo;
		this.cacheServiceEpicAddOn = cacheServiceEpicAddOn;
		this.cacheServiceSprintAddOn = cacheServiceSprintAddOn;
		this.cacheService = cacheService;
		this.cacheServiceProjectAddOn = cacheServiceProjectAddOn;
	}

	private final ProjectRepository projectRepo;

	private final EpicRepository epicRepo;

	private final SprintRepository sprintRepo;

	private final UserStoryRepository userStoryRepo;

	private final TaskRepository taskRepo;

	private final TaskAttachmentRepository taskAttachmentRepo;

	private final InvitationRepository invitationRepo;

	private final AnnouncementRepository announcementRepo;

	private final CacheServiceEpicAddOn cacheServiceEpicAddOn;

	private final CacheServiceSprintAddOn cacheServiceSprintAddOn;

	private final CacheService cacheService;

	private final CacheServiceProjectAddOn cacheServiceProjectAddOn;

	@DeleteMapping("/project/{id}")
	@Transactional
	public ResponseEntity<Void> deleteProject(@PathVariable long id) {
		
		Project project = projectRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("entity with requested Id not Found"));
		
		cacheServiceProjectAddOn.DeleteEntitiesInCacheOnProjectDeletion(project);
		
		projectRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/epic/{id}")
	@Transactional
	public ResponseEntity<Void> deleteEpic(@PathVariable long id) {

		Epic epic = epicRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("entity with requested Id not Found"));

		cacheServiceEpicAddOn.deleteEpicFromCacheAndCascadeDeleteChildren(epic);

		epicRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/sprint/{id}")
	@Transactional
	public ResponseEntity<Void> deleteSprint(@PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("entity with requested Id not Found"));

		cacheServiceSprintAddOn.deleteSprintFromCacheAndCascadeDeleteChildren(sprint);
		
		sprintRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/userstory/{id}")
	@Transactional
	public ResponseEntity<Void> deleteUserStory(@PathVariable long id) {

		UserStory userStory = userStoryRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("entity with requested Id not Found"));
		
		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.epicUserStoryListCache, userStory.getEpic().getId());
		
		cacheService.evictListFromCache(CacheServiceUserStoryAddOn.sprintUserStoryListCache, userStory.getSprint().getId());
		
		userStoryRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/task/{id}")
	@Transactional
	public ResponseEntity<Void> deleteTask(@PathVariable long id) {

		taskRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/taskattachment/{id}")
	@Transactional
	public ResponseEntity<Void> deleteTaskAttachment(@PathVariable long id) {

		taskAttachmentRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/announcement/{id}")
	@Transactional
	public ResponseEntity<Void> deleteAnnouncement(@PathVariable long id) {

		announcementRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

	@DeleteMapping("/invitation/{id}")
	@Transactional
	public ResponseEntity<Void> deleteInvitation(@PathVariable long id) {

		invitationRepo.deleteById(id);

		return ResponseEntity.status(204).build();
	}

}
