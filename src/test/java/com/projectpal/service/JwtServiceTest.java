package com.projectpal.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.projectpal.entity.User;

public class JwtServiceTest {

	@Test
	public void testGenerateToken() {

		User user = new User("haid", "haidar@gmail.com", "1234");

		JwtService jwtService = new JwtService();

		String token = jwtService.generateToken(user);

		assertNotNull(token);

		
	}
	
	@Test
	public void testIsTokenValid() {
		User user = new User("haid", "haidar@gmail.com", "1234");
		User user2 = new User("haidar", "haidartf@gmail.com", "123456");
		
		JwtService jwtService = new JwtService();

		String token = jwtService.generateToken(user);
		String token2 = jwtService.generateToken(user2);
		
		Boolean bool = jwtService.isTokenValid(token, user);
		Boolean bool2 = jwtService.isTokenValid(token2, user2);
		
		
		assertTrue(bool);
		assertTrue(bool2);
		
		Boolean mustBeFalseBool = jwtService.isTokenValid(token, user2);
		
		assertFalse(mustBeFalseBool);
	}
	
	@Test
	public void testExtractAnyClaim() {
		User user = new User("haidar", "haidar@gmail.com", "1234");
		
		JwtService jwtService = new JwtService();

		String token = jwtService.generateToken(user);
		
		assertEquals(jwtService.extractEmail(token),user.getEmail());
		
		HashMap<String, Object> map =new HashMap<>();
		map.put("role", "User");
		
		String token2 = jwtService.generateToken(map,user);
		
		String role = jwtService.extractClaim(token2,claims -> claims.get("role", String.class));
		
		assertEquals(role,"User");
	}
	
	

}
