package com.projectpal.service.admin.project;

import com.projectpal.entity.Project;

public interface AdminProjectService {

	Project findProjectById(long projectId);

	void deleteProject(long projectId);

}
