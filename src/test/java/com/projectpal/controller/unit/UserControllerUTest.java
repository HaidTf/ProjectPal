package com.projectpal.controller.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;

import com.projectpal.entity.User;
import com.projectpal.entity.Project;
import com.projectpal.entity.Task;
import com.projectpal.entity.enums.Role;
import com.projectpal.controller.UserController;
import com.projectpal.dto.request.StringHolderRequest;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.CacheServiceProjectAddOn;

@ExtendWith(MockitoExtension.class)
public class UserControllerUTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserRepository userRepo;

	@Mock
	private TaskRepository taskRepo;

	@Mock
	private ProjectRepository projectRepo;

	@Mock
	private PasswordEncoder encoder;

	@Mock
	private CacheServiceProjectAddOn cacheServiceProjectAddOn;

	private void setSecurityContext(User user) {

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
				user.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	private User getSecurityContextUser() {

		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@BeforeEach
	public void setUp() {
		User user = new User("name", "email", "password");

		user.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		Project project = new Project("title", "description");

		user.setProject(project);

		setSecurityContext(user);
	}

	// getUser() method tests:
	
	@Test
	public void testGetUser() {

		ResponseEntity<User> response = userController.getUser();

		assertEquals(response.getStatusCode().value(), 200);

		assertEquals(response.getBody().getName(), "name");
	}
	
	// updatePassword method tests:

	@Test
	public void testUpdatePassword() {
		ResponseEntity<Void> response = userController.updatePassword(new StringHolderRequest("newPassword"));

		assertEquals(response.getStatusCode().value(), 204);
	}

	// exitProject method tests:
	
	@Test
	public void testProjectUserExitsProject() {

		List<Task> tasks = new ArrayList<>();

		Task task1 = new Task();

		Task task2 = new Task();

		task1.setAssignedUser(getSecurityContextUser());

		task2.setAssignedUser(getSecurityContextUser());

		tasks.add(task1);

		tasks.add(task2);

		Mockito.when(taskRepo.findAllByAssignedUser(getSecurityContextUser())).thenReturn(Optional.of(tasks));

		ResponseEntity<Void> response = userController.exitProject();

		assertEquals(response.getStatusCode().value(), 204);

		User notInProjectUser = getSecurityContextUser();

		assertNull(notInProjectUser.getProject());

		assertEquals(notInProjectUser.getRole(), Role.ROLE_USER);

		Mockito.verify(taskRepo, Mockito.times(1)).save(task1);

		Mockito.verify(taskRepo, Mockito.times(1)).save(task2);

		Mockito.verify(userRepo, Mockito.times(0)).findAllByProject(getSecurityContextUser().getProject());

	}

	@Test
	public void testProjectOwnerExitsProjectContainsOtherUsersIncludingProjectOperator() {

		Project project = getSecurityContextUser().getProject();

		getSecurityContextUser().setRole(Role.ROLE_USER_PROJECT_OWNER);

		User user1 = Mockito.mock(User.class);

		Mockito.when(user1.getRole()).thenReturn(Role.ROLE_USER_PROJECT_OPERATOR);

		User user2 = new User();
		user2.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		List<User> projectUsers = new ArrayList<>();
		projectUsers.add(user1);
		projectUsers.add(user2);

		Mockito.when(userRepo.findAllByProject(project)).thenReturn(Optional.of(projectUsers));

		ResponseEntity<Void> response = userController.exitProject();

		User notInProjectUser = getSecurityContextUser();

		assertEquals(response.getStatusCode().value(), 204);

		assertNull(notInProjectUser.getProject());

		assertEquals(notInProjectUser.getRole(), Role.ROLE_USER);

		Mockito.verify(userRepo, Mockito.times(1)).findAllByProject(project);

		Mockito.verify(user1, Mockito.times(1)).setRole(Role.ROLE_USER_PROJECT_OWNER);

		Mockito.verify(cacheServiceProjectAddOn, Mockito.times(0)).DeleteEntitiesInCacheOnProjectDeletion(project);

		Mockito.verify(projectRepo, Mockito.times(0)).delete(project);

	}

	@Test
	public void testProjectOwnerExitsProjectContainsOtherUsersWithNoProjectOperator() {

		Project project = getSecurityContextUser().getProject();

		getSecurityContextUser().setRole(Role.ROLE_USER_PROJECT_OWNER);

		User user1 = Mockito.mock(User.class);

		Mockito.when(user1.getRole()).thenReturn(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		User user2 = new User();
		user2.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		List<User> projectUsers = new ArrayList<>();
		projectUsers.add(user1);
		projectUsers.add(user2);

		Mockito.when(userRepo.findAllByProject(project)).thenReturn(Optional.of(projectUsers));

		ResponseEntity<Void> response = userController.exitProject();

		User notInProjectUser = getSecurityContextUser();

		assertEquals(response.getStatusCode().value(), 204);

		assertNull(notInProjectUser.getProject());

		assertEquals(notInProjectUser.getRole(), Role.ROLE_USER);

		Mockito.verify(userRepo, Mockito.times(1)).findAllByProject(project);

		Mockito.verify(user1, Mockito.times(1)).setRole(Role.ROLE_USER_PROJECT_OWNER);

		Mockito.verify(cacheServiceProjectAddOn, Mockito.times(0)).DeleteEntitiesInCacheOnProjectDeletion(project);

		Mockito.verify(projectRepo, Mockito.times(0)).delete(project);

	}

	
	@Test
	public void testProjectOwnerExitsProjectDoesNotContainOtherUsers() {

		Project project = getSecurityContextUser().getProject();

		getSecurityContextUser().setRole(Role.ROLE_USER_PROJECT_OWNER);

		ResponseEntity<Void> response = userController.exitProject();

		assertEquals(response.getStatusCode().value(), 204);

		User notInProjectUser = getSecurityContextUser();

		assertNull(notInProjectUser.getProject());

		assertEquals(notInProjectUser.getRole(), Role.ROLE_USER);

		Mockito.verify(cacheServiceProjectAddOn, Mockito.times(1)).DeleteEntitiesInCacheOnProjectDeletion(project);

		Mockito.verify(projectRepo, Mockito.times(1)).delete(project);
	}

}
