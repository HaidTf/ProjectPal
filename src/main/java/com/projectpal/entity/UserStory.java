package com.projectpal.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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


@Entity
public class UserStory {
	public UserStory() {
		tasks = new ArrayList<Task>();
	}
	
	public UserStory(String name, String description, Byte priority, Epic epic, Sprint sprint) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = Progress.TODO;
		this.epic = epic;
		this.sprint = sprint;
		tasks = new ArrayList<Task>();
		
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	@NonNull
	private String name;

	private String description;

	@Column(columnDefinition = "TINYINT")
	private Byte priority;
	
	@Enumerated(EnumType.STRING)
	@NonNull
	private Progress progress;
	
	@ManyToOne
	@JsonIgnore
	private Epic epic;
	
	@ManyToOne
	@JsonIgnore
	private Sprint sprint;
	
	@OneToMany(mappedBy = "userStory",cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Task> tasks;
	
	//Getters and Setters

	public Epic getEpic() {
		return epic;
	}

	public void setEpic(Epic epic) {
		this.epic = epic;
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

	public Sprint getSprint() {
		return sprint;
	}

	public void setSprint(Sprint sprint) {
		this.sprint = sprint;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public void addTask(Task task) {
		tasks.add(task);
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}
	@JsonProperty("progress")
	private void setInitialProgress(Progress progress) {
		if (progress == null)
			this.progress = Progress.TODO;
		else {
			this.progress = progress;
		}

	}
	
	
}
