package com.projectpal.service;

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
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.SprintRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.cache.CacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepo;

	private final UserRepository userRepo;

	private final EpicRepository epicRepo;

	private final SprintRepository sprintRepo;

	private final TaskService taskService;

	private final CacheService<Project> cacheService;

	@Transactional(readOnly = true)
	public ProjectResponseDto findProjectDtoById(long id) {

		return projectRepo.findProjectDtoById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	@Transactional
	public void createProjectAndSetOwner(Project project, User user) {

		project.setOwner(user);

		projectRepo.save(project);

		user.setRole(Role.ROLE_USER_PROJECT_OWNER);

		user.setProject(project);

		userRepo.save(user);
	}

	@Transactional
	public void updateProjectDescription(Project project, @Nullable String description) {

		project.setDescription(description);

		projectRepo.save(project);

	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
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
	public void cascadeDeleteChildrenOfProjectInCache(Project project) {

		cacheService.evictListFromCache(Epic.EPIC_CACHE, project.getId());
		cacheService.evictListFromCache(Sprint.SPRINT_CACHE, project.getId());

		List<Epic> epics = epicRepo.findAllByProject(project);
		List<Sprint> sprints = sprintRepo.findAllByProject(project);

		epics.forEach((epic) -> cacheService.evictListFromCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId()));
		sprints.forEach((sprint) -> cacheService.evictListFromCache(UserStory.SPRINT_USERSTORY_CACHE, sprint.getId()));
	}

}
