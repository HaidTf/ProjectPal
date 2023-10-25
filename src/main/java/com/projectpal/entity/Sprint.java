package com.projectpal.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Getter
@Setter
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

	public Sprint(String name, String description, LocalDate startDate, LocalDate endDate, Progress progress) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.progress = progress;
	}

	private static final long serialVersionUID = 4L;

	public static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("creationDate", "startDate", "endDate");

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotBlank
	@Size(min = 3, max = 60)
	private String name;

	@Nullable
	@Size(max = 300)
	private String description;

	@Temporal(TemporalType.DATE)
	@NotNull
	private LocalDate startDate;

	@Temporal(TemporalType.DATE)
	@NotNull
	private LocalDate endDate;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Progress progress;

	@Temporal(TemporalType.DATE)
	@CreatedDate
	@Setter(AccessLevel.NONE)
	private LocalDate creationDate;

	@ManyToOne
	@JsonIgnore
	private Project project;

}
