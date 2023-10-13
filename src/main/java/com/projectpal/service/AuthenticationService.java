package com.projectpal.service;

import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projectpal.dto.request.AuthenticationRequest;
import com.projectpal.dto.request.RegisterRequest;
import com.projectpal.dto.response.AuthenticationResponse;

import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.UserRepository;
import com.projectpal.security.token.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository repo;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	private final AuthenticationManager authManager;

	public AuthenticationResponse register(RegisterRequest req) {

		User user = new User(req.getName(), req.getEmail(), passwordEncoder.encode(req.getPassword()));

		user.setRole(Role.ROLE_USER);

		user.setCreationDate(LocalDate.now());

		repo.save(user);

		String jwtToken = jwtService.generateToken(user);

		return new AuthenticationResponse(jwtToken);

	}

	public AuthenticationResponse authenticate(AuthenticationRequest req) {

		authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

		User user = repo.findUserByEmail(req.getEmail()).orElseThrow();

		String jwtToken = jwtService.generateToken(user);

		return new AuthenticationResponse(jwtToken);
	}

}
