package com.projectpal.service.user;

import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import com.projectpal.dto.response.entity.ProjectMemberResponseDto;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;

public interface UserService {

	public User findUserById(long userId);

	public Page<User> findAllByProjectAndRole(Project project, @Nullable Role role, int page, int size);

	public Page<ProjectMemberResponseDto> findProjectMembersDtoListByProjectAndRole(Project project,
			@Nullable Role role, int page, int size);

	public void updateUserPassword(User user, String password);

	public void updateUserProjectRole(User currentUser, long userId, Role role);

	public void exitUserProject(User currentUser);

}
