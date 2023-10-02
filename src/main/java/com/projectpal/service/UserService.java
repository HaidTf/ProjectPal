package com.projectpal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.ConflictException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.utils.MaxAllowedUtil;

@Service
public class UserService {

	@Autowired
	public UserService(PasswordEncoder encoder, UserRepository userRepo, ProjectRepository projectRepo,
			TaskService taskService, ProjectService projectService) {
		this.encoder = encoder;
		this.userRepo = userRepo;
		this.projectRepo = projectRepo;
		this.taskService = taskService;
		this.projectService = projectService;
	}

	private final PasswordEncoder encoder;

	private final UserRepository userRepo;

	private final ProjectRepository projectRepo;

	private final TaskService taskService;

	private final ProjectService projectService;

	public User findUserById(long userId) {
		return userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("no user with this id is found"));
	}

	public Page<User> findAllByProjectAndRole(Project project, @Nullable Role role, int page, int size) {

		if (size > MaxAllowedUtil.MAX_PAGE_SIZE)
			throw new ConflictException("Page size exceeded size limit");

		Pageable pageable = PageRequest.of(page, size);

		if (role == null)
			return userRepo.findAllByProjectAndRole(project, role, pageable);
		else {
			return userRepo.findAllByProject(project, pageable);
		}

	}

	public void updateUserPassword(User user, String password) {
		user.setPassword(encoder.encode(password));
		userRepo.save(user);
	}

	public void updateUserRole(User user, Role role) {
		user.setRole(role);
		userRepo.save(user);
	}

	public void exitUserProject(User user) {

		Project project = user.getProject();

		user.setProject(null);
		userRepo.save(user);

		if (user.getRole() == Role.ROLE_USER_PROJECT_OWNER) {

			Optional<List<User>> projectUsers = userRepo.findAllByProject(project);

			if (projectUsers.isPresent() && projectUsers.get().size() > 0) {

				boolean newProjectOwnerIsSet = false;

				for (User projectUser : projectUsers.get()) {

					if (projectUser.getRole() == Role.ROLE_USER_PROJECT_OPERATOR) {

						projectUser.setRole(Role.ROLE_USER_PROJECT_OWNER);
						userRepo.save(projectUser);

						project.setOwner(projectUser);
						projectRepo.save(project);

						newProjectOwnerIsSet = true;
						break;
					}
				}

				if (!newProjectOwnerIsSet) {

					User newProjectOwner = projectUsers.get().get(0);
					newProjectOwner.setRole(Role.ROLE_USER_PROJECT_OWNER);
					userRepo.save(newProjectOwner);

					project.setOwner(newProjectOwner);
					projectRepo.save(project);
				}
			} else {
				projectService.cascadeDeleteChildrenOfProjectInCache(project);
				projectRepo.delete(project);
			}
		}

		user.setRole(Role.ROLE_USER);
		userRepo.save(user);

		taskService.exitUserTasks(user);
	}

}
