package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {

	Optional<List<Epic>> findAllByProject(Project project);
	
	Optional<List<Epic>> findAllByProjectId(Long projectId);
	
	int countByProjectId(Long projectId);

	Optional<List<Epic>> findAllByProjectIdAndProgressNot(long id, Progress progress);
}
