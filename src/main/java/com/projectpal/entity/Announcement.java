package com.projectpal.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Announcement {

	@JsonCreator
	public Announcement(String title, String description) {
		this.title = title;
		this.description = description;
		this.issueDate = LocalDate.now();
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	private String title;
	
	private String description;
	
	@Temporal(TemporalType.DATE)
	private LocalDate issueDate;
	
	@ManyToOne
	private Project project;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}
}
