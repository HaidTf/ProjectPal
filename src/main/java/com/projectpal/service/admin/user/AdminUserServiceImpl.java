package com.projectpal.service.admin.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Project;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.client.EntityNotFoundException;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.project.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepo;

	private final TaskRepository taskRepo;

	private final ProjectRepository projectRepo;

	private final ProjectService projectService;

	@Override
	@Transactional(readOnly = true)
	public User findUserById(long userId) {

		return userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class));
	}

	@Override
	@Transactional
	public void deleteUser(long userId) {

		User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class));

		Optional<Project> project = user.getOptionalOfProject();

		if (project.isPresent()) {

			user.setProject(null);
			userRepo.save(user);

			if (user.getRole() == Role.ROLE_USER_PROJECT_OWNER) {

				List<User> projectUsers = userRepo.findAllByProject(project.get());

				if (projectUsers.size() > 0) {

					boolean newProjectOwnerIsSet = false;

					for (User projectUser : projectUsers) {

						if (projectUser.getRole() == Role.ROLE_USER_PROJECT_OPERATOR) {

							projectUser.setRole(Role.ROLE_USER_PROJECT_OWNER);
							userRepo.save(projectUser);

							project.get().setOwner(projectUser);
							projectRepo.save(project.get());

							newProjectOwnerIsSet = true;
							break;
						}
					}

					if (!newProjectOwnerIsSet) {

						User newProjectOwner = projectUsers.get(0);
						newProjectOwner.setRole(Role.ROLE_USER_PROJECT_OWNER);
						userRepo.save(newProjectOwner);

						project.get().setOwner(newProjectOwner);
						projectRepo.save(project.get());
					}
				} else {
					projectService.cascadeDeleteChildrenOfProjectInCache(project.get());
					projectRepo.delete(project.get());
				}
			}

			List<Task> tasks = taskRepo.findAllByAssignedUser(user);

			if (tasks.size() > 0) {

				for (Task task : tasks) {

					task.setAssignedUser(null);
					taskRepo.save(task);

				}
			}
		}

		userRepo.delete(user);
	}

}
