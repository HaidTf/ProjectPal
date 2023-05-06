package com.projectpal.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectpal.entity.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Table(name = "users")
@Entity
public class User implements UserDetails {
	
	public User(String name, String email, String password, Role role,Project project) {
		this.project = project;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	@Transient
	private static final long serialVersionUID = 1234L;

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@Column(unique = true)
	@NonNull
	private String name;

	@Column(unique = true)
	@NonNull
	private String email;

	@NonNull
	@JsonIgnore
	private String password;

	@Enumerated(EnumType.STRING)
	@NonNull
	private Role role;
	
	@ManyToOne
	private Project project;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Invitation> invitations;

	// Getters and Setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.toString()));
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public String getUsername() {
		
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<Invitation> getInvitations() {
		return invitations;
	}

	public void setInvitations(List<Invitation> invitations) {
		this.invitations = invitations;
	}
	
	public void addInvitation(Invitation invite) {
		this.invitations.add(invite);
	}

}
