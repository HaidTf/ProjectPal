package com.projectpal.security.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projectpal.exception.client.CustomAuthenticationException;
import com.projectpal.security.entrypoint.CustomAuthenticationEntryPoint;
import com.projectpal.security.token.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	private final UserDetailsService userDetailsService;

	private final CustomAuthenticationEntryPoint authEntryPoint;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		final String jwt;
		final String userEmail;

		final String uri = request.getRequestURI();

		if (uri.startsWith("/api/auth/") || uri.startsWith("/api/docs")) {

			log.debug("JwtAuthenticationFilter allows passage of request due to uri prefix");

			filterChain.doFilter(request, response);
			return;

		}

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			log.debug(
					"Jwt Authentication failed: No Jwt token is in the Authorization header; request commenced to AuthenticationEntryPoint");

			authEntryPoint.commence(request, response, new CustomAuthenticationException(
					"Jwt Authentication failed: No Jwt token is in the Authorization header"));
			return;
		}

		try {

			jwt = authHeader.substring(7);

			userEmail = jwtService.extractEmail(jwt);

		} catch (IndexOutOfBoundsException ex) {
			
			log.debug(
					"Jwt Authentication failed: No Jwt token is in the Authorization header; request commenced to AuthenticationEntryPoint");
			
			authEntryPoint.commence(request, response, new CustomAuthenticationException(
					"Jwt Authentication failed: No Jwt token is in the Authorization header", ex));
			return;

		} catch (ExpiredJwtException ex) {
			
			log.debug(
					"Jwt Authentication failed: Jwt token is expired; request commenced to AuthenticationEntryPoint");
			
			authEntryPoint.commence(request, response,
					new CustomAuthenticationException("Jwt Authentication failed: Jwt token is expired", ex));
			return;

		}

		if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails;

			try {

				userDetails = this.userDetailsService.loadUserByUsername(userEmail);

			} catch (UsernameNotFoundException ex) {

				log.debug(
						"Jwt Authentication failed: User not found; request commenced to AuthenticationEntryPoint");
				
				authEntryPoint.commence(request, response,
						new CustomAuthenticationException("Jwt Authentication failed: User not found", ex));
				return;

			}

			if (jwtService.isTokenValid(jwt, userDetails)) {

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
				
				log.debug("Jwt authentication success, security context populated");
			}
		}
		filterChain.doFilter(request, response);
	}

}
