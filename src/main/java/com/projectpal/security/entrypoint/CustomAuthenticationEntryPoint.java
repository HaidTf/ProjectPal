package com.projectpal.security.entrypoint;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		log.debug("CustomAuthenticationEntryPoint invoked due to exception: {}", authException.getMessage());

		response.setStatus(401);

		response.addHeader("WWW-Authenticate", "Bearer");

		response.setContentType("application/json;charset=UTF-8");

		String jsonResponse = new StringBuilder().append("{\"message\":").append("\"")
				.append(authException.getMessage()).append("\"}").toString();

		response.getWriter().write(jsonResponse);
	}
}
