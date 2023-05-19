package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findUserByEmail(String email);

	Optional<User> findUserByName(String name);

	Optional<List<User>> findAllByProject(Project project);

	Optional<List<User>> findAllByRole(Role role);

	@Modifying
	@Query("UPDATE User u SET u.email = ?2 WHERE u.id = ?1")
	void updateEmailById(Long id, String email);

	@Modifying
	@Query("UPDATE User u SET u.password = ?2 WHERE u.id = ?1")
	void updatePasswordById(Long id, String password);

	@Modifying
	@Query("UPDATE User u SET u.role = ?2 WHERE u.id = ?1")
	void updateRoleById(Long id, Role role);

}
