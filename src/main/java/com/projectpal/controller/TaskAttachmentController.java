package com.projectpal.controller;

//This class is not suitable for production and is implemented just for the sake of reducing the complexity resulted by the integration with third party storage services

//MARKED FOR REMOVAL: Integration with 3rd party service is in replacement

//@RestController
//@RequestMapping("/attachment")
//public class TaskAttachmentController {
//
//	@Autowired
//	public TaskAttachmentController(TaskAttachmentRepository attachmentRepo, TaskRepository taskRepo,
//			FileStorageService fileStorageService) {
//		this.attachmentRepo = attachmentRepo;
//		this.taskRepo = taskRepo;
//		this.fileStorageService = fileStorageService;
//	}
//
//	private final TaskAttachmentRepository attachmentRepo;
//
//	private final TaskRepository taskRepo;
//
//	private final FileStorageService fileStorageService;
//
//	private static final long MAX_FILE_SIZE = (long) (0.5 * 1024 * 1024);
//
//	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
//	@PostMapping("/upload")
//	@Transactional
//	public ResponseEntity<Void> uploadFile(@RequestParam MultipartFile file, @RequestParam long taskId) {
//
//		if (file.isEmpty())
//			throw new BadRequestException("no file uploaded");
//
//		if (file.getSize() > MAX_FILE_SIZE)
//			throw new BadRequestException("file size exceeds the limit");
//
//		fileStorageService.storeFile(file, taskId);
//
//		Task task = taskRepo.findById(taskId)
//				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));
//
//		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
//			throw new ForbiddenException("you are not allowed to access other projects");
//
//		TaskAttachment attachment = new TaskAttachment(file.getName());
//
//		attachmentRepo.save(attachment);
//
//		return ResponseEntity.noContent().build();
//
//	}
//
//	@GetMapping("/list/{taskId}")
//	public ResponseEntity<List<TaskAttachment>> getTaskAttachments(@PathVariable long taskId) {
//
//		Task task = taskRepo.findById(taskId)
//				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));
//
//		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
//			throw new ForbiddenException("you are not allowed to access other projects");
//
//		List<TaskAttachment> attachments = attachmentRepo.findAllByTask(task)
//				.orElseThrow(() -> new ResourceNotFoundException("no taskAttachments with this task id are found"));
//		;
//
//		return ResponseEntity.ok(attachments);
//	}
//
//	@GetMapping("/download/{taskId}/{fileName}")
//	public ResponseEntity<Resource> downloadFile(@PathVariable long taskId, @PathVariable String fileName) {
//
//		Task task = taskRepo.findById(taskId)
//				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));
//
//		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
//			throw new ForbiddenException("you are not allowed to access other projects");
//
//		Resource resource = fileStorageService.loadFile(fileName, taskId);
//
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, new StringBuilder().append("attachment; filename=\"").append(resource.getFilename()).append("\"").toString())
//				.body(resource);
//	}
//
//	@DeleteMapping("/delete/{taskId}/{fileName}")
//	@Transactional
//	public ResponseEntity<Void> deleteFile(@PathVariable long taskId, @PathVariable String fileName) {
//
//		Task task = taskRepo.findById(taskId)
//				.orElseThrow(() -> new ResourceNotFoundException("no task with this id is found"));
//
//		if (task.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
//			throw new ForbiddenException("you are not allowed to access other projects");
//
//		TaskAttachment attachment = attachmentRepo.findTaskAttachmentByFileNameAndTaskId(fileName, taskId)
//				.orElseThrow(() -> new ResourceNotFoundException("no taskAttachment with this name is found"));
//		;
//
//		if (attachment.getTask().getId() != task.getId())
//			throw new ForbiddenException("you are not allowed to access other projects");
//
//		attachmentRepo.delete(attachment);
//
//		fileStorageService.deleteFile(fileName, taskId);
//
//		return ResponseEntity.status(204).build();
//	}
//
//}
