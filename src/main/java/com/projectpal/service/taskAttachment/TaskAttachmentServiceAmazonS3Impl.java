package com.projectpal.service.taskAttachment;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projectpal.entity.TaskAttachment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskAttachmentServiceAmazonS3Impl implements TaskAttachmentService {

	/*
	 * TODO: Integration with Amazon S3 (Aws sdk version 2.x)
	 */

	@Override
	public List<TaskAttachment> findAttachmentsByTaskId(long taskId) {

		// TODO find attachments from DB (does not include the actual files)

		return null;
	}

	@Override
	public URL getAttachmentDownloadUrl(long attachmentId) {

		// TODO compose presigned url for getting object

		return null;
	}

	@Override
	public TaskAttachment createAttachment(MultipartFile file) {

		// TODO create TaskAttachment at DB level and save file in S3

		return null;
	}

	@Override
	public void deleteAttachment(long attachmentId) {

		// TODO delete TaskAttachment from DB and delete file from S3

	}

}
