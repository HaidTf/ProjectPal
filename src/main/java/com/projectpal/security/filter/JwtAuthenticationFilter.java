package com.projectpal.security.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projectpal.exception.CustomAuthenticationException;
import com.projectpal.security.CustomAuthenticationEntryPoint;
import com.projectpal.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
			CustomAuthenticationEntryPoint authEntryPoint) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.authEntryPoint = authEntryPoint;
	}

	private final JwtService jwtService;

	private final UserDetailsService userDetailsService;

	private final CustomAuthenticationEntryPoint authEntryPoint;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			authEntryPoint.commence(request, response, new CustomAuthenticationException(
					"Jwt Authentication failed: No Jwt token is in the Authorization header"));
			return;
		}

		try {

			jwt = authHeader.substring(7);

			userEmail = jwtService.extractEmail(jwt);

		} catch (IndexOutOfBoundsException ex) {
			authEntryPoint.commence(request, response, new CustomAuthenticationException(
					"Jwt Authentication failed: No Jwt token is in the Authorization header", ex));
			return;
			
		} catch (ExpiredJwtException ex) {
			authEntryPoint.commence(request, response,
					new CustomAuthenticationException("Jwt Authentication failed: Jwt token is expired", ex));
			return;

		}
		
		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails;

			try {

				userDetails = this.userDetailsService.loadUserByUsername(userEmail);

			} catch (UsernameNotFoundException ex) {

				authEntryPoint.commence(request, response,
						new CustomAuthenticationException("Jwt Authentication failed: User not found", ex));
				return;

			}

			if (jwtService.isTokenValid(jwt, userDetails)) {

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
