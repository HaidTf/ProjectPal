package com.projectpal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Invitation {

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	@ManyToOne
	private User user;
	
	@ManyToOne
	private Project project;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
}
