package com.projectpal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.AnnouncementRepository;
import com.projectpal.utils.ProjectUtil;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

	@Autowired
	public AnnouncementController(AnnouncementRepository announcementRepo) {
		this.announcementRepo = announcementRepo;
	}

	private final AnnouncementRepository announcementRepo;
	
	
	@GetMapping("/list")
	public ResponseEntity<List<Announcement>> getAnnouncements(){
		
		Project project = ProjectUtil.getProjectNotNull();
		
		List<Announcement> announcements = announcementRepo.findAllByProject(project).orElseThrow(()-> new ResourceNotFoundException("project not found"));
		
		announcements.sort((announcement1,announcement2)-> announcement1.getIssueDate().compareTo(announcement2.getIssueDate()));
		
		return ResponseEntity.ok(announcements); 
	}
	
	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createAnnouncement(@RequestBody Announcement announcement){
		
		if(announcement==null||announcement.getTitle()==null||announcement.getDescription()==null) 
			throw new BadRequestException("request is or contains null values");
		
		announcement.setProject(ProjectUtil.getProjectNotNull());
		
		announcementRepo.save(announcement);
		
		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/announcement").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
			
	}
	
	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteAnnouncement(@PathVariable long id) {
		
		Announcement announcement = announcementRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("announcement not found"));
		
		if(announcement.getProject().getId()!= ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");
		
		announcementRepo.delete(announcement);
		
		return ResponseEntity.status(204).build();
	}
}
