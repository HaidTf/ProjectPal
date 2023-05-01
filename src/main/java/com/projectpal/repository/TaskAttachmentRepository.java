package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.TaskAttachment;
@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment,Long> {

}