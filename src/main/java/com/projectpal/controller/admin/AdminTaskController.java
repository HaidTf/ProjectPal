package com.projectpal.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.entity.Task;
import com.projectpal.service.admin.task.AdminTaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/tasks/{taskId}")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
@RequiredArgsConstructor
public class AdminTaskController {

	private final AdminTaskService taskService;

	@GetMapping
	public ResponseEntity<Task> getTask(@PathVariable long taskId) {

		return ResponseEntity.ok(taskService.findTaskById(taskId));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteTask(@PathVariable long taskId) {

		taskService.deleteTask(taskId);

		return ResponseEntity.status(204).build();
	}

}
