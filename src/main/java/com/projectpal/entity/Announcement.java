package com.projectpal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Announcement {

	public Announcement(String title, String description) {
		this.title = title;
		this.description = description;
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	private String title;
	
	private String description;
	
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
}
