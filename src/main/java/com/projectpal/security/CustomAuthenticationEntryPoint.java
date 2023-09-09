package com.projectpal.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		response.setStatus(401);

		response.setContentType("application/json;charset=UTF-8");

		String jsonResponse = new StringBuilder().append("{\"message\":").append("\"").append(authException.getMessage())
				.append("\"}").toString();

		response.getWriter().write(jsonResponse);
	}
}
