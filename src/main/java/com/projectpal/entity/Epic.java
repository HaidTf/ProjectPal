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
public class Epic {

	public Epic () {
		userStories= new ArrayList<UserStory>();
	}
	
	public Epic(String name, String description, Byte priority, Project project) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.project = project;
		userStories = new ArrayList<UserStory>();
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
	private Project project;
	
	@OneToMany(mappedBy="epic",cascade = CascadeType.REMOVE)
	private List<UserStory> userStories;
	
	//Getters and Setters

	public List<UserStory> getUserStories() {
		return userStories;
	}

	public void setUserStories(List<UserStory> userStories) {
		this.userStories = userStories;
		
	}

	public void addUserStory(UserStory userStory) {
		this.userStories.add(userStory);
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

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

	public Byte getPriority() {
		return priority;
	}

	public void setPriority(Byte priority) {
		this.priority = priority;
	}

}
