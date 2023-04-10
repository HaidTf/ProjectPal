package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectpal.entity.Project;

public interface ProjectRepository extends JpaRepository<Project,Long>{

}
