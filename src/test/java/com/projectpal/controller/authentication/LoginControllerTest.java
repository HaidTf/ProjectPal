package com.projectpal.controller.authentication;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("development")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class LoginControllerTest {

	@Autowired
	public LoginControllerTest(MockMvc mockMvc, PasswordEncoder encoder, UserRepository repo) {
		this.mockMvc = mockMvc;
		this.encoder = encoder;
		this.repo = repo;
	}

	private final MockMvc mockMvc;

	private final PasswordEncoder encoder;

	private final UserRepository repo;

	@Test
	public void testLoginWithFullData() throws Exception {

		Map<String, String> loginData = new HashMap<>();
		loginData.put("email", "haidar@gmail.com");
		loginData.put("password", "12345");

		User user = new User("haidar", loginData.get("email"), encoder.encode(loginData.get("password")));

		user.setRole(Role.ROLE_USER);

		repo.save(user);

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(loginData);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String responseBody = result.getResponse().getContentAsString();

		JSONObject json = new JSONObject(responseBody);

		String token = json.getString("token");

		assertNotNull(token);

	}

	@Test
	public void testLoginWithNoData() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void testLoginWithMissingData() throws Exception {

		Map<String, String> loginData = new HashMap<>();
		// missing email value
		loginData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(loginData);

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void testLoginWithIncorrectCredentials() throws Exception {

		Map<String, String> loginData = new HashMap<>();
		loginData.put("email", "haidart@gmail.com");
		loginData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(loginData);

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());

	}
}
