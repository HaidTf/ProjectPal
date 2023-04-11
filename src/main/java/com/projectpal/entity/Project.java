package com.projectpal.entity;

import org.springframework.lang.NonNull;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Project {

	public Project(String name, String description, User user) {
		this.name = name;
		this.description = description;
		this.user = user;
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@NonNull
	private String name;

	private String description;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "owner_id")
	private User user;
	
	//Getters and Setters

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
