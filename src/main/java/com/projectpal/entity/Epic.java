package com.projectpal.entity;

import org.springframework.lang.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Epic {

	public Epic(String name, String description, Byte priority, Project project) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.project = project;
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
	
	//Getters and Setters

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
