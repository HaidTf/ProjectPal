package com.projectpal.entity;

import java.time.LocalDate;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpal.entity.enums.Progress;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Sprint {
	
	public Sprint(String name, String description, LocalDate startDate, LocalDate endDate, Project project) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.progress = Progress.TODO;
		this.project = project;
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;
	
	@NonNull
	private String name;

	private String description;

	@Temporal(TemporalType.DATE)
	private LocalDate startDate;
	@Temporal(TemporalType.DATE)
	private LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	@NonNull
	private Progress progress;

	@ManyToOne
	@JsonIgnore
	private Project project;
	
	//Getters and Setters

	public long getId() {
		return id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
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

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(Progress progress) {
		this.progress = progress;
	}

}
