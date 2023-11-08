package com.projectpal.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class Project implements Serializable {

	public Project(String name, String description) {
		this.name = name;
		this.description = description;
	}

	private static final long serialVersionUID = 2L;

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

	@Temporal(TemporalType.DATE)
	@CreatedDate
	@Setter(AccessLevel.NONE)
	@Column(nullable = false)
	private LocalDate creationDate;

	@OneToOne
	@JoinColumn(name = "owner_id")
	private User owner;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Epic> epics;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Sprint> sprints;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Invitation> invitations;

	@OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Announcement> announcements;

}
