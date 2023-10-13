package com.projectpal.entity;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Announcement {

	@JsonCreator
	public Announcement(String title, String description) {
		this.title = title;
		this.description = description;
		this.issueDate = LocalDate.now();
	}

	public static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("issueDate");

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotBlank
	@Size(min = 3, max = 60)
	private String title;

	@Nullable
	@Size(max = 300)
	private String description;

	@Temporal(TemporalType.DATE)
	private LocalDate issueDate;

	@ManyToOne
	@JsonIgnore
	private Project project;

}
