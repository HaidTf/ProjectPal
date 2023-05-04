package com.projectpal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.repository.AnnouncementRepository;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

	@Autowired
	public AnnouncementController(AnnouncementRepository announcementRepo) {
		this.announcementRepo = announcementRepo;
	}

	private final AnnouncementRepository announcementRepo;
}
