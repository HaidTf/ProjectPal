package com.projectpal.service.admin.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Project;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.service.project.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProjectServiceImpl implements AdminProjectService {

	private final ProjectRepository projectRepo;

	private final ProjectService projectService;

	@Override
	@Transactional(readOnly = true)
	public Project findProjectById(long projectId) {
		return projectRepo.findById(projectId).orElseThrow(() -> new EntityNotFoundException(Project.class));
	}

	@Override
	@Transactional
	public void deleteProject(long projectId) {

		Project project = projectRepo.findById(projectId).orElseThrow(() -> new EntityNotFoundException(Project.class));

		projectRepo.delete(project);

		projectService.cascadeDeleteChildrenOfProjectInCache(project);

	}

}
