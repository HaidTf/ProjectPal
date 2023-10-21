package com.projectpal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectpal.dto.response.entity.ProjectResponseDto;
import com.projectpal.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

	@Query("SELECT new com.projectpal.dto.response.entity.ProjectResponseDto(p.id, p.name, p.description, u.id, u.name) FROM Project p JOIN p.owner u where p.id = :id")
	Optional<ProjectResponseDto> findProjectDtoById(@Param("id") long id);

}
