package com.projectpal.service.admin.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Task;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminTaskServiceImpl implements AdminTaskService {

	private final TaskRepository taskRepo;

	@Override
	@Transactional(readOnly = true)
	public Task findTaskById(long taskId) {
		return taskRepo.findById(taskId).orElseThrow(() -> new EntityNotFoundException(Task.class));
	}

	@Override
	@Transactional
	public void deleteTask(long taskId) {
		taskRepo.deleteById(taskId);
	}
}
