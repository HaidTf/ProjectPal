package com.projectpal.entity;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Invitation {

	public Invitation(User invitedUser, Project project) {
		this.invitedUser = invitedUser;
		this.project = project;
	}

	public static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("issueDate");

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@Temporal(TemporalType.DATE)
	@CreatedDate
	@Setter(AccessLevel.NONE)
	private LocalDate issueDate;

	@ManyToOne
	private User invitedUser;

	@ManyToOne
	private Project project;

}
