package com.projectpal.service.taskAttachment;

import java.net.URL;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.projectpal.entity.TaskAttachment;

public interface TaskAttachmentService {

	public List<TaskAttachment> findAttachmentsByTaskId(long taskId);
	
	public URL getAttachmentDownloadUrl(long attachmentId);
	
	public TaskAttachment createAttachment(MultipartFile file);
	
	public void deleteAttachment(long attachmentId);
	
	
}
