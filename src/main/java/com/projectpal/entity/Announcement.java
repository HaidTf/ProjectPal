package com.projectpal.entity;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Announcement {

	public Announcement(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("issueDate");

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotBlank
	@Size(min = 3, max = 100)
	@Column(columnDefinition = "VARCHAR(100)", nullable = false)
	private String title;

	@Nullable
	@Size(max = 300)
	@Column(columnDefinition = "VARCHAR(300)")
	private String description;

	@Temporal(TemporalType.DATE)
	@CreatedDate
	@Setter(AccessLevel.NONE)
	private LocalDate issueDate;

	@ManyToOne
	@JsonIgnore
	private Project project;

	@ManyToOne
	private User announcer;

}
