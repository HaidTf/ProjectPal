package com.projectpal.service.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.dto.request.authentication.AuthenticationRequest;
import com.projectpal.dto.request.authentication.RegisterRequest;
import com.projectpal.dto.response.AuthenticationResponse;

import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.UserRepository;
import com.projectpal.security.token.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final UserRepository repo;

	private final PasswordEncoder passwordEncoder;

	private final JwtService jwtService;

	private final AuthenticationManager authManager;

	@Override
	@Transactional
	public AuthenticationResponse register(RegisterRequest req) {

		User user = new User(req.getName(), req.getEmail(), passwordEncoder.encode(req.getPassword()));

		user.setRole(Role.ROLE_USER);

		repo.save(user);

		String jwtToken = jwtService.generateToken(user);

		return new AuthenticationResponse(jwtToken);

	}

	@Override
	@Transactional(readOnly = true, noRollbackFor = Exception.class)
	public AuthenticationResponse authenticate(AuthenticationRequest req) {

		User user = (User) authManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()))
				.getPrincipal();

		String jwtToken = jwtService.generateToken(user);

		return new AuthenticationResponse(jwtToken);
	}

}
