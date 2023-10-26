package com.projectpal.service.project;

import org.springframework.lang.Nullable;

import com.projectpal.dto.response.entity.ProjectResponseDto;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;

public interface ProjectService {

	public ProjectResponseDto findProjectDtoById(long id);

	public void createProjectAndSetOwner(Project project, User user);

	public void updateProjectDescription(Project project, @Nullable String description);

	public void removeUserFromCurrentUserProject(User currentUser, long userId);

	public void deleteProject(Project project);

	public void cascadeDeleteChildrenOfProjectInCache(Project project);

}
