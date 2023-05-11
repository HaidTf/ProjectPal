package com.projectpal.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long>{

	void deleteByLastAccessedDateBefore(LocalDate xDateAgo);

}
