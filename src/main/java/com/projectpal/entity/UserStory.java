package com.projectpal.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class UserStory implements Serializable {

	public UserStory(String name, String description, int priority) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = Progress.TODO;
	}

	public UserStory(String name, String description, int priority, Progress progress) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = progress;
	}

	private static final long serialVersionUID = 5L;

	public static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("creationDate", "priority");

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotBlank
	@Size(min = 3, max = 60)
	@Column(columnDefinition = "VARCHAR(60)", nullable = false)
	private String name;

	@Nullable
	@Size(max = 300)
	@Column(columnDefinition = "VARCHAR(300)")
	private String description;

	@Min(1)
	@Max(10)
	@NotNull
	@Column(columnDefinition = "TINYINT", nullable = false)
	private int priority;

	@Enumerated(EnumType.STRING)
	@NotNull
	@Column(nullable = false)
	private Progress progress;

	@Temporal(TemporalType.DATE)
	@CreatedDate
	@Setter(AccessLevel.NONE)
	private LocalDate creationDate;

	@ManyToOne
	@JsonIgnore
	private Epic epic;

	@ManyToOne
	@JsonIgnore
	private Sprint sprint;

	@OneToMany(mappedBy = "userStory", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Task> tasks;

}
