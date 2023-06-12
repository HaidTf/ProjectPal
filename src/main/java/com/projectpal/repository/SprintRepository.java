package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
@Repository
public interface SprintRepository extends JpaRepository<Sprint,Long>{

	Optional<List<Sprint>> findAllByProject(Project project);

	Optional<List<Sprint>> findAllByProjectId(Long id);
	
	int countByProjectId(Long projectId);
	
}
