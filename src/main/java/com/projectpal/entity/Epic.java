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
public class Epic implements Serializable {

	public Epic(String name, String description, int priority) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = Progress.TODO;
	}

	public Epic(String name, String description, int priority, Progress progress) {
		this.name = name;
		this.description = description;
		this.priority = priority;
		this.progress = progress;
	}

	private static final long serialVersionUID = 3L;

	public static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("creationDate", "priority");

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NotBlank
	@Size(min = 3, max = 60)
	private String name;

	@Nullable
	@Size(max = 300)
	private String description;

	@Column(columnDefinition = "TINYINT")
	@Min(1)
	@Max(10)
	@NotNull
	private int priority;

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

	@OneToMany(mappedBy = "epic", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<UserStory> userStories;

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Epic other = (Epic) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
