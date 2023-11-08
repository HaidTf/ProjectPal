package com.projectpal.service.admin.taskattachment;

import java.net.URL;

import com.projectpal.entity.TaskAttachment;

public interface AdminTaskAttachmentService {

	public TaskAttachment getTaskAttachment(long attachmentId);

	public URL getAttachmentDownloadUrl(long attachmentId);

	public void deleteAttachment(long attachmentId);
}
