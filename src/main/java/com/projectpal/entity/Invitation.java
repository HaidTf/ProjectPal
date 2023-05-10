package com.projectpal.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Invitation {

	public Invitation(User invitedUser, Project project) {
		this.invitedUser = invitedUser;
		this.project = project;
		this.issueDate = LocalDate.now();
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	@Temporal(TemporalType.DATE)
	private LocalDate issueDate;
	
	@ManyToOne
	private User invitedUser;
	
	@ManyToOne
	private Project project;

	public User getUser() {
		return invitedUser;
	}

	public void setUser(User user) {
		this.invitedUser = user;
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
