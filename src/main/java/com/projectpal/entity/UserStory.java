package com.projectpal.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpal.entity.enums.Progress;

import jakarta.annotation.Nullable;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
public class UserStory implements Serializable {
	
	@JsonCreator
	public UserStory(String name, String description, int priority) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = Progress.TODO;
		this.creationDate = LocalDate.now();
	}
	 
	public UserStory(String name, String description, int priority,Progress progress) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = progress;
	}
	
	@Transient
	private static final long serialVersionUID = 5L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	@NotBlank
	@Size(min=3,max=60)
	private String name;

	@Nullable
	@Size(max=300)
	private String description;

	@Column(columnDefinition = "TINYINT")
	@Min(1)
	@Max(250)
	@NotNull
	private int priority;
	
	@Enumerated(EnumType.STRING)
	private Progress progress;
	
	@Temporal(TemporalType.DATE)
	private LocalDate creationDate;
	
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
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
	
	
}
