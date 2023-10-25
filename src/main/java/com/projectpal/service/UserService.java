package com.projectpal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.dto.response.entity.ProjectMemberResponseDto;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.validation.PageValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final PasswordEncoder encoder;

	private final UserRepository userRepo;

	private final ProjectRepository projectRepo;

	private final TaskService taskService;

	private final ProjectService projectService;

	@Transactional(readOnly = true)
	public User findUserById(long userId) {
		return userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("no user with this id is found"));
	}

	@Transactional(readOnly = true)
	public Page<User> findAllByProjectAndRole(Project project, @Nullable Role role, int page, int size) {

		PageValidator.validatePage(page, size);

		Pageable pageable = PageRequest.of(page, size);

		if (role != null)
			return userRepo.findAllByProjectAndRole(project, role, pageable);
		else {
			return userRepo.findAllByProject(project, pageable);
		}

	}

	@Transactional(readOnly = true)
	public Page<ProjectMemberResponseDto> findProjectMembersDtoListByProjectAndRole(Project project,
			@Nullable Role role, int page, int size) {

		PageValidator.validatePage(page, size);

		Pageable pageable = PageRequest.of(page, size);

		if (role != null)
			return userRepo.findProjectMembersDtoListByProjectAndRole(project, role, pageable);
		else {
			return userRepo.findProjectMembersDtoListByProject(project, pageable);
		}

	}

	@Transactional
	public void updateUserPassword(User user, String password) {
		user.setPassword(encoder.encode(password));
		userRepo.save(user);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void updateUserProjectRole(User currentUser, long userId, Role role) {

		User user = userRepo.findUserByIdAndProject(userId, currentUser.getProject())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (user.getProject().getId() != currentUser.getProject().getId())
			throw new ForbiddenException("the user must be in the project");

		if (currentUser.getId() == userId)
			throw new BadRequestException("you can not change your own role");

		if (role != Role.ROLE_USER_PROJECT_PARTICIPATOR || role != Role.ROLE_USER_PROJECT_OPERATOR)
			throw new BadRequestException("You are not allowed to set this role");

		user.setRole(role);
		userRepo.save(user);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void exitUserProject(User currentUser) {

		Project project = currentUser.getProject();

		currentUser.setProject(null);
		userRepo.save(currentUser);

		if (currentUser.getRole() == Role.ROLE_USER_PROJECT_OWNER) {

			List<User> projectUsers = userRepo.findAllByProject(project);

			if (projectUsers.size() > 0) {

				boolean newProjectOwnerIsSet = false;

				for (User projectUser : projectUsers) {

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

					User newProjectOwner = projectUsers.get(0);
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

		currentUser.setRole(Role.ROLE_USER);
		userRepo.save(currentUser);

		taskService.exitUserTasks(currentUser);
	}

}
