package com.projectpal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projectpal.entity.Task;
import com.projectpal.entity.TaskAttachment;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.TaskAttachmentRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.service.FileStorageService;
import com.projectpal.utils.ProjectUtil;

//This class is not suitable for production and is implemented just for the sake of reducing the complexity resulted by the integration with third party storage services

@RestController
@RequestMapping("/attachment")
public class TaskAttachmentController {

	@Autowired
	public TaskAttachmentController(TaskAttachmentRepository attachmentRepo, TaskRepository taskRepo,
			FileStorageService fileStorageService) {
		this.attachmentRepo = attachmentRepo;
		this.taskRepo = taskRepo;
		this.fileStorageService = fileStorageService;
	}

	private final TaskAttachmentRepository attachmentRepo;

	private final TaskRepository taskRepo;

	private final FileStorageService fileStorageService;

	private static final long MAX_FILE_SIZE = (long) (0.5 * 1024 * 1024);

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/upload")
	@Transactional
	public ResponseEntity<Void> uploadFile(@RequestParam MultipartFile file, @RequestParam long taskId) {

		if (file.isEmpty())
			throw new BadRequestException("no file uploaded");

		if (file.getSize() > MAX_FILE_SIZE)
			throw new BadRequestException("file size exceeds the limit");

		fileStorageService.storeFile(file, taskId);

		Task task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		TaskAttachment attachment = new TaskAttachment(file.getName());

		attachmentRepo.save(attachment);

		return ResponseEntity.noContent().build();

	}

	@GetMapping("/list/{taskId}")
	public ResponseEntity<List<TaskAttachment>> getTaskAttachments(@PathVariable long taskId) {

		Task task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		List<TaskAttachment> attachments = attachmentRepo.findAllByTask(task)
				.orElseThrow(() -> new ResourceNotFoundException("no taskAttachments with this task id are found"));
		;

		return ResponseEntity.ok(attachments);
	}

	@GetMapping("/download/{taskId}/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable long taskId, @PathVariable String fileName) {

		Task task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		Resource resource = fileStorageService.loadFile(fileName, taskId);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, new StringBuilder().append("attachment; filename=\"").append(resource.getFilename()).append("\"").toString())
				.body(resource);
	}

	@DeleteMapping("/delete/{taskId}/{fileName}")
	@Transactional
	public ResponseEntity<Void> deleteFile(@PathVariable long taskId, @PathVariable String fileName) {

		Task task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));

		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		TaskAttachment attachment = attachmentRepo.findTaskAttachmentByFileNameAndTaskId(fileName, taskId)
				.orElseThrow(() -> new ResourceNotFoundException("no taskAttachment with this name is found"));
		;

		if (attachment.getTask().getId() != task.getId())
			throw new ForbiddenException("you are not allowed to access other projects");

		attachmentRepo.delete(attachment);

		fileStorageService.deleteFile(fileName, taskId);

		return ResponseEntity.status(204).build();
	}

}
