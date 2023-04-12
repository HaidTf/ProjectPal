package com.projectpal.entity;

import org.springframework.lang.NonNull;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class TaskAttachment {

	public TaskAttachment(String fileName, Task task) {
		this.fileName = fileName;
		this.task = task;
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NonNull
	private String fileName;

	@ManyToOne
	private Task task;

	// Getters and Setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String name) {
		this.fileName = name;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
