package com.projectpal.controller;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.dto.response.ListHolderResponse;
import com.projectpal.dto.response.UrlResponseDto;
import com.projectpal.entity.TaskAttachment;
import com.projectpal.service.taskAttachment.TaskAttachmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskAttachmentController {

	private final TaskAttachmentService taskAttachmentService;

	@GetMapping("/{taskId}/attachments")
	public ResponseEntity<ListHolderResponse<TaskAttachment>> getTaskAttachments(@PathVariable long taskId) {

		log.debug("API:GET/tasks/{}/attachments invoked", taskId);

		List<TaskAttachment> attachments = taskAttachmentService.findAttachmentsByTaskId(taskId);

		return ResponseEntity.ok(new ListHolderResponse<TaskAttachment>(attachments));

	}

	@GetMapping("/attachments/{attachmentId}")
	public ResponseEntity<UrlResponseDto> getAttachmentDownloadUrl(@PathVariable long attachmentId) {

		log.debug("API:GET/tasks/attachments/{} invoked", attachmentId);

		URL url = taskAttachmentService.getAttachmentDownloadUrl(attachmentId);

		return ResponseEntity.ok(new UrlResponseDto(url.toString()));
	}

	@PostMapping("/{taskId}/attachments")
	public ResponseEntity<TaskAttachment> createAttachment(@PathVariable long taskId,
			@RequestParam("file") MultipartFile file) {

		log.debug("API:GET/tasks/{}/attachments invoked", taskId);

		TaskAttachment attachment = taskAttachmentService.createAttachment(taskId, file);

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/tasks/attachments/" + attachment.getId()).build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).body(attachment);

	}

	@DeleteMapping("/attachments/{attachmentId}")
	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	public ResponseEntity<Void> deleteAttachment(@PathVariable long attachmentId) {

		log.debug("API:DELETE/tasks/attachments/{} invoked", attachmentId);

		taskAttachmentService.deleteAttachment(attachmentId);

		return ResponseEntity.status(204).build();

	}
}
