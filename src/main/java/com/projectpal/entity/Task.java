package com.projectpal.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
public class Task {

	public Task(String name, String description, Byte priority, UserStory userStory, User assignedUser) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.userStory = userStory;
		this.assignedUser = assignedUser;
		taskAttachments = new ArrayList<TaskAttachment>();
	}
	
	public Task() {
		taskAttachments = new ArrayList<TaskAttachment>();
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	@NonNull
	private String name;

	private String description;

	@Column(columnDefinition = "TINYINT")
	private Byte priority;

	@ManyToOne
	private UserStory userStory;

	@ManyToOne
	private User assignedUser;

	@OneToMany(mappedBy = "task", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
	private List<TaskAttachment> taskAttachments;

	// Getters and Setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserStory getUserStory() {
		return userStory;
	}

	public void setUserStory(UserStory userStory) {
		this.userStory = userStory;
	}

	public Byte getPriority() {
		return priority;
	}

	public void setPriority(Byte priority) {
		this.priority = priority;
	}

	public User getAssignedUser() {
		return assignedUser;
	}

	public void setAssignedUser(User assignedUser) {
		this.assignedUser = assignedUser;
	}

	public List<TaskAttachment> getTaskAttachments() {
		return taskAttachments;
	}

	public void setTaskAttachments(List<TaskAttachment> taskAttachments) {
		this.taskAttachments = taskAttachments;
	}

	public void addTaskAttachment(TaskAttachment taskAttachment) {
		taskAttachments.add(taskAttachment);
	}

}
