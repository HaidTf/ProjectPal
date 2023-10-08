package com.projectpal.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.UserRepository;

@Service
public class ProjectService {

	@Autowired
	public ProjectService(ProjectRepository projectRepo, UserRepository userRepo, EpicService epicService,
			SprintService sprintService, CacheService<Project> cacheService, UserService userService,
			TaskService taskService) {
		this.projectRepo = projectRepo;
		this.userRepo = userRepo;
		this.epicService = epicService;
		this.sprintService = sprintService;
		this.userService = userService;
		this.taskService = taskService;
		this.cacheService = cacheService;
	}

	private final ProjectRepository projectRepo;

	private final UserRepository userRepo;

	private final EpicService epicService;

	private final SprintService sprintService;

	private final UserService userService;

	private final TaskService taskService;

	private final CacheService<Project> cacheService;

	public void createProjectAndSetOwner(Project project, User user) {

		project.setOwner(user);

		projectRepo.save(project);

		user.setRole(Role.ROLE_USER_PROJECT_OWNER);

		user.setProject(project);

		userRepo.save(user);
	}

	public void updateProjectDescription(Project project, @Nullable String description) {

		project.setDescription(description);

		projectRepo.save(project);

	}

	public void updateProjectLastAccessedDate(Project project) {

		project.setLastAccessedDate(LocalDate.now());

		projectRepo.save(project);
	}

	public void removeUserFromCurrentUserProject(User currentUser, long userId) {

		if (currentUser.getId() == userId)
			throw new BadRequestException("You cant remove yourself from the project through here");

		User user = userService.findUserById(userId);

		if (user.getProject().getId() != currentUser.getProject().getId())
			throw new ForbiddenException("the user must be in the project");

		user.setProject(null);
		user.setRole(Role.ROLE_USER);
		userRepo.save(user);

		taskService.exitUserTasks(user);
	}

	public void deleteProject(Project project) {

		List<User> projectUsers = userRepo.findAllByProject(project).orElse(new ArrayList<User>(0));

		for (User projectUser : projectUsers) {
			projectUser.setRole(Role.ROLE_USER);
			userRepo.save(projectUser);
		}

		this.cascadeDeleteChildrenOfProjectInCache(project);

		projectRepo.delete(project);

	}

	public void cascadeDeleteChildrenOfProjectInCache(Project project) {

		cacheService.evictListFromCache(Epic.EPIC_CACHE, project.getId());
		cacheService.evictListFromCache(Sprint.SPRINT_CACHE, project.getId());

		List<Epic> epics = epicService.findEpicsByProjectAndProgressFromDb(project, Set.of(), Sort.unsorted()).get();
		List<Sprint> sprints = sprintService.findSprintsByProjectAndProgressFromDb(project, Set.of(), Sort.unsorted())
				.get();

		epics.forEach((epic) -> cacheService.evictListFromCache(UserStory.EPIC_USERSTORY_CACHE, epic.getId()));
		sprints.forEach((sprint) -> cacheService.evictListFromCache(UserStory.SPRINT_USERSTORY_CACHE, sprint.getId()));
	}

}
