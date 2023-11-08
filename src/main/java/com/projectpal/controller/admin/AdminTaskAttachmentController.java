package com.projectpal.controller.admin;

import java.net.URL;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.dto.response.UrlResponseDto;
import com.projectpal.entity.TaskAttachment;
import com.projectpal.service.admin.taskattachment.AdminTaskAttachmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/taskattachments/{attachmentId}")
@RequiredArgsConstructor
public class AdminTaskAttachmentController {

	private final AdminTaskAttachmentService attachmentService;

	@GetMapping()
	public ResponseEntity<TaskAttachment> getTaskAttachmentById(@PathVariable long attachmentId) {
		return ResponseEntity.ok(attachmentService.getTaskAttachment(attachmentId));
	}

	@GetMapping("/url")
	public ResponseEntity<UrlResponseDto> getAttachmentDowloadUrl(@PathVariable long attachmentId) {

		URL url = attachmentService.getAttachmentDownloadUrl(attachmentId);

		return ResponseEntity.ok(new UrlResponseDto(url.toString()));

	}

	@DeleteMapping()
	public ResponseEntity<Void> deleteTaskAttachment(@PathVariable long attachmentId) {

		attachmentService.deleteAttachment(attachmentId);

		return ResponseEntity.status(204).build();

	}

}
