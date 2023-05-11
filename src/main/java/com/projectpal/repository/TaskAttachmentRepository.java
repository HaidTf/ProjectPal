package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.projectpal.entity.Task;
import com.projectpal.entity.TaskAttachment;

@Repository
public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment,Long> {

	Optional<List<TaskAttachment>> findAllByTask(Task task);

}
