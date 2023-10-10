package com.projectpal.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.enums.Progress;
@Repository
public interface SprintRepository extends JpaRepository<Sprint,Long>{

	Optional<List<Sprint>> findAllByProject(Project project);

	Optional<List<Sprint>> findAllByProjectId(Long id);
	
	int countByProjectId(Long projectId);

	Optional<List<Sprint>> findAllByProjectIdAndProgressNot(long id, Progress progress);

	Optional<List<Sprint>> findAllByProjectAndProgressIn(Project project, Set<Progress> progress, Sort sort);

	Optional<List<Sprint>> findAllByProject(Project project, Sort sort);

	Optional<List<Sprint>> findAllByProjectAndProgressIn(Project project, Set<Progress> progress);
	
}
