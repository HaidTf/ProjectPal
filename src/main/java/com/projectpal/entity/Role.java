package com.projectpal.entity;

import com.projectpal.entity.enums.Roles;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Role {

	public Role(Roles role) {
		this.role = role;
	}

	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	private long id;

	@Enumerated(EnumType.STRING)
	private Roles role;

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (role != other.getRole())
			return false;
		return true;
	}

}
