package com.projectpal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.repository.TaskAttachmentRepository;
import com.projectpal.utils.SecurityContextUtil;

@RestController
@RequestMapping("/attachment")
public class TaskAttachmentController {
	@Autowired
	public TaskAttachmentController( TaskAttachmentRepository attachmentRepo) {
		this.attachmentRepo = attachmentRepo;
	}

	private final TaskAttachmentRepository attachmentRepo;
}
