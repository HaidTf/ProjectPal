package com.projectpal.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpal.entity.enums.Progress;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Sprint implements Serializable {

	@JsonCreator
	public Sprint(String name, String description, LocalDate startDate, LocalDate endDate) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.progress = startDate.isBefore(LocalDate.now()) ? Progress.INPROGRESS : Progress.TODO;
		this.creationDate = LocalDate.now();
	}
	 
	public Sprint(String name, String description, LocalDate startDate, LocalDate endDate,Progress progress) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.progress = progress;
	}
	
	@Transient
	private static final long serialVersionUID = 4L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotBlank
	@Size(min=3,max=60)
	private String name;

	@Nullable
	@Size(max=300)
	private String description;

	@Temporal(TemporalType.DATE)
	@NotNull
	private LocalDate startDate;
	
	@Temporal(TemporalType.DATE)
	@NotNull
	private LocalDate endDate;

	@Enumerated(EnumType.STRING)
	private Progress progress;
	
	@Temporal(TemporalType.DATE)
	private LocalDate creationDate;

	@ManyToOne
	@JsonIgnore
	private Project project;

	// Getters and Setters

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
		if (startDate.isBefore(LocalDate.now()))
			this.setProgress(Progress.INPROGRESS);
		else
			this.setProgress(Progress.TODO);
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
	
	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

}
