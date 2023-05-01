package com.projectpal.entity;

import java.util.ArrayList;
import java.util.List;


import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;


@Entity
public class Project {
	
	public Project() {
		this.epics = new ArrayList<Epic>();
		this.sprints = new ArrayList<Sprint>();
		
	}

	public Project(String name, String description, User user) {
		this.name = name;
		this.description = description;
		this.owner = user;
		this.epics = new ArrayList<Epic>();
		this.sprints = new ArrayList<Sprint>();
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NonNull
	private String name;

	private String description;
	
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
	
	//Getters and Setters

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

}
