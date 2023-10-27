package com.projectpal.service.project;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.dto.response.entity.ProjectResponseDto;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.client.BadRequestException;
import com.projectpal.exception.client.ForbiddenException;
import com.projectpal.exception.client.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.SprintRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.cache.CacheConstants;
import com.projectpal.service.cache.CacheService;
import com.projectpal.service.task.TaskService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepo;

	private final UserRepository userRepo;

	private final EpicRepository epicRepo;

	private final SprintRepository sprintRepo;

	private final TaskService taskService;

	private final CacheService<Project> cacheService;

	@Transactional(readOnly = true)
	@Override
	public ProjectResponseDto findProjectDtoById(long id) {

		return projectRepo.findProjectDtoById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	@Transactional
	@Override
	public void createProjectAndSetOwner(Project project, User user) {

		project.setOwner(user);

		projectRepo.save(project);

		user.setRole(Role.ROLE_USER_PROJECT_OWNER);

		user.setProject(project);

		userRepo.save(user);
	}

	@Transactional
	@Override
	public void updateProjectDescription(Project project, @Nullable String description) {

		project.setDescription(description);

		projectRepo.save(project);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void removeUserFromCurrentUserProject(User currentUser, long userId) {

		if (currentUser.getId() == userId)
			throw new BadRequestException("You cant remove yourself from the project through here");

		User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

		if (user.getProject().getId() != currentUser.getProject().getId())
			throw new ForbiddenException("the user must be in the project");

		user.setProject(null);
		user.setRole(Role.ROLE_USER);
		userRepo.save(user);

		taskService.exitUserTasks(user);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	@Override
	public void deleteProject(Project project) {

		List<User> projectUsers = userRepo.findAllByProject(project);

		for (User projectUser : projectUsers) {
			projectUser.setRole(Role.ROLE_USER);
			userRepo.save(projectUser);
		}

		this.cascadeDeleteChildrenOfProjectInCache(project);

		projectRepo.delete(project);

	}

	@Transactional
	@Override
	public void cascadeDeleteChildrenOfProjectInCache(Project project) {

		cacheService.evictCache(CacheConstants.EPIC_CACHE, project.getId());
		cacheService.evictCache(CacheConstants.SPRINT_CACHE, project.getId());

		List<Epic> epics = epicRepo.findAllByProject(project);
		List<Sprint> sprints = sprintRepo.findAllByProject(project);

		epics.forEach((epic) -> cacheService.evictCache(CacheConstants.EPIC_USERSTORY_CACHE, epic.getId()));
		sprints.forEach(
				(sprint) -> cacheService.evictCache(CacheConstants.SPRINT_USERSTORY_CACHE, sprint.getId()));
	}

}
