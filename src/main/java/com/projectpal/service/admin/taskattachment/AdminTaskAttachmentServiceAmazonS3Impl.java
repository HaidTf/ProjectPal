package com.projectpal.service.admin.taskattachment;

import java.net.URL;

import org.springframework.stereotype.Service;

import com.projectpal.entity.TaskAttachment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminTaskAttachmentServiceAmazonS3Impl implements AdminTaskAttachmentService {

	// TODO: Integration with Amazon S3 (Aws sdk version 2.x)

	@Override
	public TaskAttachment getTaskAttachment(long attachmentId) {
		return null;
	}

	@Override
	public URL getAttachmentDownloadUrl(long attachmentId) {
		return null;
	}

	@Override
	public void deleteAttachment(long attachmentId) {

	}

}
