package com.projectpal.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Project implements Serializable {

	@JsonCreator
	public Project(String name, String description) {
		this.name = name;
		this.description = description;
		this.lastAccessedDate = LocalDate.now();
		this.creationDate = LocalDate.now();
	}
	
	@Transient
	private static final long serialVersionUID = 2L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotNull
	private String name;

	private String description;
	
	@Temporal(TemporalType.DATE)
	@JsonIgnore
	private LocalDate lastAccessedDate;
	
	@Temporal(TemporalType.DATE)
	private LocalDate creationDate;

	@OneToOne
	@JoinColumn(name = "owner_id")
	@JsonIgnore
	private User owner;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Epic> epics;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Sprint> sprints;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Invitation> invitations;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Announcement> announcements;

	// Getters and Setters

	public User getOwner() {
		return owner;
	}

	public void setOwner(User user) {
		this.owner = user;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Epic> getEpics() {
		return epics;
	}

	public void setEpics(List<Epic> epics) {
		this.epics = epics;
	}

	public void addEpic(Epic epic) {
		epics.add(epic);
	}

	public List<Sprint> getSprints() {
		return sprints;
	}

	public void setSprints(List<Sprint> sprints) {
		this.sprints = sprints;
	}

	public void addSprint(Sprint sprint) {
		sprints.add(sprint);
	}

	public List<Invitation> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<Invitation> invitations) {
		this.invitations = invitations;
	}

	public void addInvitation(Invitation invite) {
		this.invitations.add(invite);
	}

	public List<Announcement> getAnnouncements() {
		return announcements;
	}

	public void setAnnouncements(List<Announcement> announcements) {
		this.announcements = announcements;
	}

	public void addAnnouncement(Announcement announcement) {
		this.announcements.add(announcement);
	}

	public LocalDate getLastAccessedDate() {
		return lastAccessedDate;
	}

	public void setLastAccessedDate(LocalDate lastAccessedDate) {
		this.lastAccessedDate = lastAccessedDate;
	}
	
	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

}
