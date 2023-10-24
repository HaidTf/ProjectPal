package com.projectpal.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {

	Optional<Epic> findByIdAndProject(long epicId,Project project);
	
	List<Epic> findAllByProject(Project project);
	
	List<Epic> findAllByProjectId(Long projectId);
	
	int countByProjectId(Long projectId);

	List<Epic> findAllByProjectIdAndProgressNot(long id, Progress progress);

	List<Epic> findAllByProjectAndProgressIn(Project project, Set<Progress> progress, Sort sort);

	List<Epic> findAllByProject(Project project, Sort sort);

	List<Epic> findAllByProjectAndProgressIn(Project project, Set<Progress> progress);

}
