package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectpal.entity.TaskAttachments;

public interface TaskAttachmentsRepository extends JpaRepository<TaskAttachments,Long> {

}
