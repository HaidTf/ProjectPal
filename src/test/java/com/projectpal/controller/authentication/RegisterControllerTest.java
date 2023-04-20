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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testRegisterWithFullData() throws Exception {

		Map<String, String> registerData = new HashMap<>();
		registerData.put("name", "haidar");
		registerData.put("email", "haidar@gmail.com");
		registerData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(registerData);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
						.content(requestBody).accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		String responseBody = result.getResponse().getContentAsString();

		JSONObject json = new JSONObject(responseBody);

		String token = json.getString("token");

		JwtService jwtService = new JwtService();

		String mustBeEqualToken = jwtService.generateToken(new User(registerData.get("name"), registerData.get("email"),
				registerData.get("password"), Role.USER, null));

		assertNotNull(token);

		assertEquals(token, mustBeEqualToken);
	}

	@Test
	public void testRegisterWithNoData() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	@Test
	public void testRegisterWithMissingData() throws Exception {
		
		Map<String, String> registerData = new HashMap<>();
		//missing name value
		registerData.put("email", "haidar@gmail.com");
		registerData.put("password", "12345");

		ObjectMapper objectMapper = new ObjectMapper();

		String requestBody = objectMapper.writeValueAsString(registerData);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(MockMvcResultMatchers.status().isBadRequest());

	}

}
