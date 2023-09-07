package com.projectpal.controller.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.repository.TaskRepository;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.JwtService;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("development")
@Transactional
public class UserControllerTest {

	@Autowired
	public UserControllerTest(UserRepository userRepo, ProjectRepository projectRepo, TaskRepository taskRepo,
			JwtService jwtService, MockMvc mockMvc, ObjectMapper objectMapper) {
		this.userRepo = userRepo;
		this.projectRepo = projectRepo;
		this.taskRepo = taskRepo;
		this.jwtService = jwtService;
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
	}

	private final MockMvc mockMvc;

	private final UserRepository userRepo;

	private final ProjectRepository projectRepo;

	private final TaskRepository taskRepo;

	private final JwtService jwtService;

	private final ObjectMapper objectMapper;

	private String token;

	private String authHeader;

	private User setUpUser(Role role) {
		User user = new User("name", "email", "password");

		user.setRole(role);

		userRepo.save(user);

		token = jwtService.generateToken(user);

		authHeader = "Bearer " + token;

		return user;
	}

	// getUser() method tests:

	@Test
	public void testGetUser() throws Exception {

		setUpUser(Role.ROLE_USER);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.get("/user").header(HttpHeaders.AUTHORIZATION, authHeader)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String jsonString = result.getResponse().getContentAsString();

		JSONObject json = new JSONObject(jsonString);

		assertEquals("name", json.get("name"));
		assertEquals("email", json.get("email"));
	}

	// updatePassword() method tests:

	@Test
	public void testUpdatePassword() throws Exception {

		setUpUser(Role.ROLE_USER);

		Map<String, String> passwordHolder = new HashMap<>();

		passwordHolder.put("password", "newPassword");

		String jsonPasswordHolder = objectMapper.writeValueAsString(passwordHolder);

		mockMvc.perform(MockMvcRequestBuilders.patch("/user/update/password")
				.header(HttpHeaders.AUTHORIZATION, authHeader).contentType(MediaType.APPLICATION_JSON)
				.content(jsonPasswordHolder).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		Optional<User> mustBeUpdatedUser = userRepo.findUserByName("name");

		assertNotEquals("password", mustBeUpdatedUser.get().getPassword());
	}

	@Test
	public void testUpdatePasswordAuthorization() throws Exception {

		setUpUser(Role.ROLE_SUPER_ADMIN);

		Map<String, String> passwordHolder = new HashMap<>();

		passwordHolder.put("password", "newPassword");

		String jsonPasswordHolder = objectMapper.writeValueAsString(passwordHolder);

		mockMvc.perform(MockMvcRequestBuilders.patch("/user/update/password")
				.header(HttpHeaders.AUTHORIZATION, authHeader).contentType(MediaType.APPLICATION_JSON)
				.content(jsonPasswordHolder).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isForbidden());

	}

	@Test
	public void testUpdatePasswordBeanValidation() throws Exception {

		setUpUser(Role.ROLE_USER);

		Map<String, String> passwordHolder = new HashMap<>();

		passwordHolder.put("password", "");

		String jsonPasswordHolder = objectMapper.writeValueAsString(passwordHolder);

		mockMvc.perform(MockMvcRequestBuilders.patch("/user/update/password")
				.header(HttpHeaders.AUTHORIZATION, authHeader).contentType(MediaType.APPLICATION_JSON)
				.content(jsonPasswordHolder).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	public void testUpdatePasswordWithNoRequestBody() throws Exception {

		setUpUser(Role.ROLE_USER);

		mockMvc.perform(
				MockMvcRequestBuilders.patch("/user/update/password").header(HttpHeaders.AUTHORIZATION, authHeader)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	// exitProject() method tests:

	@Test
	public void testProjectUserExitsProject() throws Exception {

		User user = setUpUser(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		Project project = new Project("project", "description");

		projectRepo.save(project);

		user.setProject(project);

		userRepo.save(user);

		Task task1 = new Task("task", "desc", 5);

		Task task2 = new Task("task", "desc", 5);

		task1.setAssignedUser(user);

		task2.setAssignedUser(user);

		taskRepo.save(task1);

		taskRepo.save(task2);

		mockMvc.perform(
				MockMvcRequestBuilders.patch("/user/update/project/exit").header(HttpHeaders.AUTHORIZATION, authHeader)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		assertNull(user.getProject());

		assertEquals(Role.ROLE_USER, user.getRole());

		assertNull(task1.getAssignedUser());

		assertNull(task2.getAssignedUser());
	}

	@Test
	public void testProjectOwnerExitsProjectDoesNotContainOtherUsers() throws Exception {

		User user = setUpUser(Role.ROLE_USER_PROJECT_OWNER);

		Project project = new Project("project", "description");

		project.setOwner(user);

		projectRepo.save(project);

		user.setProject(project);

		userRepo.save(user);

		long projectId = project.getId();

		mockMvc.perform(
				MockMvcRequestBuilders.patch("/user/update/project/exit").header(HttpHeaders.AUTHORIZATION, authHeader)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		Optional<Project> mustBeDeletedProject = projectRepo.findById(projectId);

		assertTrue(mustBeDeletedProject.isEmpty());

		assertNull(user.getProject());

		assertEquals(Role.ROLE_USER, user.getRole());
	}

	@Test
	public void testProjectOwnerExitsProjectContainsOtherUsersIncludingProjectOperator() throws Exception {

		User user = setUpUser(Role.ROLE_USER_PROJECT_OWNER);

		Project project = new Project("project", "description");

		project.setOwner(user);

		projectRepo.save(project);

		user.setProject(project);

		userRepo.save(user);

		User user1 = new User("name1", "email1", "password");

		user1.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		User user2 = new User("name2", "email2", "password");

		user2.setRole(Role.ROLE_USER_PROJECT_OPERATOR);

		user1.setProject(project);

		user2.setProject(project);

		userRepo.save(user1);
		userRepo.save(user2);

		mockMvc.perform(
				MockMvcRequestBuilders.patch("/user/update/project/exit").header(HttpHeaders.AUTHORIZATION, authHeader)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		assertNull(user.getProject());

		assertEquals(Role.ROLE_USER, user.getRole());

		assertEquals(Role.ROLE_USER_PROJECT_OWNER, user2.getRole());

	}

	@Test
	public void testProjectOwnerExitsProjectContainsOtherUsersWithNoProjectOperator() throws Exception {

		User user = setUpUser(Role.ROLE_USER_PROJECT_OWNER);

		Project project = new Project("project", "description");

		project.setOwner(user);

		projectRepo.save(project);

		user.setProject(project);

		userRepo.save(user);

		User user1 = new User("name1", "email1", "password");

		user1.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		User user2 = new User("name2", "email2", "password");

		user2.setRole(Role.ROLE_USER_PROJECT_PARTICIPATOR);

		user1.setProject(project);

		user2.setProject(project);

		userRepo.save(user1);
		userRepo.save(user2);

		mockMvc.perform(
				MockMvcRequestBuilders.patch("/user/update/project/exit").header(HttpHeaders.AUTHORIZATION, authHeader)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		assertNull(user.getProject());

		assertEquals(Role.ROLE_USER, user.getRole());

		assertEquals(Role.ROLE_USER_PROJECT_OWNER, user1.getRole());
	}

}
