package com.projectpal.entity;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpal.entity.enums.Progress;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
public class Task implements Serializable {

	@JsonCreator
	public Task(String name, String description, int priority) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = Progress.TODO;
		this.creationDate = LocalDate.now();
	}
	 
	public Task(String name, String description, int priority,Progress progress) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = progress;
	}
	
	@Transient
	private static final long serialVersionUID = 6L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	@NotNull
	private String name;

	private String description;

	@Column(columnDefinition = "TINYINT")
	@Min(1)
	@Max(250)
	@NotNull
	private int priority;
	
	@Enumerated(EnumType.STRING)
	private Progress progress;
	
	private String report;
	
	@Temporal(TemporalType.DATE)
	private LocalDate creationDate;

	@ManyToOne
	@JsonIgnore
	private UserStory userStory;

	@ManyToOne
	private User assignedUser;
	
	@ManyToOne
	@JsonIgnore
	private Project project;

	@OneToMany(mappedBy = "task", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
	@JsonIgnore
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}
	
	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
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

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

}
