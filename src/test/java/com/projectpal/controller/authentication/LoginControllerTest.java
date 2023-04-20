package com.projectpal.controller.authentication;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.UserRepository;
import com.projectpal.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository repo;

	@Test
	public void testLoginWithFullData() throws Exception {

		Map<String, String> loginData = new HashMap<>();
		loginData.put("email", "haidar@gmail.com");
		loginData.put("password", "12345");

		repo.save(
				new User("haidar", loginData.get("email"), encoder.encode(loginData.get("password")), Role.USER, null));

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(loginData);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String responseBody = result.getResponse().getContentAsString();

		JSONObject json = new JSONObject(responseBody);

		String token = json.getString("token");

		JwtService jwtService = new JwtService();

		String mustBeEqualToken = jwtService
				.generateToken(new User("", loginData.get("email"), loginData.get("password"), Role.USER, null));

		assertNotNull(token);

		assertEquals(token, mustBeEqualToken);
	}

	@Test
	public void testLoginWithNoData() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void testLoginWithMissingData() throws Exception {

		Map<String, String> loginData = new HashMap<>();
		// missing email value
		loginData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(loginData);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
}
