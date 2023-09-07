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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("development")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RegisterControllerTest {

	@Autowired
	public RegisterControllerTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	private final MockMvc mockMvc;

	@Test
	@Transactional
	public void testRegisterWithFullData() throws Exception {

		Map<String, String> registerData = new HashMap<>();
		registerData.put("name", "haidar12"); 
		registerData.put("email", "haidar12@gmail.com");
		registerData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(registerData);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String responseBody = result.getResponse().getContentAsString();

		JSONObject json = new JSONObject(responseBody);

		String token = json.getString("token");

		assertNotNull(token);

	}

	@Test
	@Transactional
	public void testRegisterWithNoData() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/auth/register").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	@Transactional
	public void testRegisterWithMissingData() throws Exception {

		Map<String, String> registerData = new HashMap<>();
		// missing name value
		registerData.put("email", "haidar@gmail.com");
		registerData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(registerData);

		mockMvc.perform(MockMvcRequestBuilders.post("/auth/register").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

	@Test
	public void testExceptionHandler() throws Exception {
		Map<String, String> registerData = new HashMap<>();
		registerData.put("name", "haidar");
		registerData.put("email", "haidar@gmail.com");
		registerData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(registerData);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String responseBody = result.getResponse().getContentAsString();

		JSONObject json = new JSONObject(responseBody);

		String token = json.getString("token");

		assertNotNull(token);

		registerData.replace("email", "haidar@gmail.com", "htf@gmail.com");

		MvcResult result2 = mockMvc
				.perform(MockMvcRequestBuilders.post("/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(409)).andReturn();

		String responseBody2 = result2.getResponse().getContentAsString();

		JSONObject json2 = new JSONObject(responseBody2);

		String constraintViolated = json2.getString("constraintViolated");

		assertTrue(constraintViolated.startsWith("users.UK"));

	}

}
