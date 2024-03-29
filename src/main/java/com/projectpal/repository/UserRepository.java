package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectpal.dto.response.entity.ProjectMemberResponseDto;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findUserByEmail(String email);

	Optional<User> findUserByName(String name);

	Optional<User> findUserByIdAndProject(long userId, Project project);

	List<User> findAllByProject(Project project);

	List<User> findAllByRole(Role role);

	@Modifying
	@Query("UPDATE User u SET u.email = ?2 WHERE u.id = ?1")
	void updateEmailById(Long id, String email);

	@Modifying
	@Query("UPDATE User u SET u.password = ?2 WHERE u.id = ?1")
	void updatePasswordById(Long id, String password);

	@Modifying
	@Query("UPDATE User u SET u.role = ?2 WHERE u.id = ?1")
	void updateRoleById(Long id, Role role);

	Page<User> findAllByProjectAndRole(Project project, Role role, Pageable pageable);

	Page<User> findAllByProject(Project project, Pageable pageable);

	@Query("SELECT new com.projectpal.dto.response.entity.ProjectMemberResponseDto(u.id,u.name,u.role) FROM User u WHERE u.project = :project AND u.role = :role")
	Page<ProjectMemberResponseDto> findProjectMembersDtoListByProjectAndRole(@Param("project") Project project,
			@Param("role") Role role, Pageable pageable);

	@Query("SELECT new com.projectpal.dto.response.entity.ProjectMemberResponseDto(u.id,u.name,u.role) FROM User u WHERE u.project = :project")
	Page<ProjectMemberResponseDto> findProjectMembersDtoListByProject(@Param("project") Project project,
			Pageable pageable);

	Page<User> findAllByRole(Role role, Pageable pageable);

}
