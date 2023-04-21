package com.projectpal.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projectpal.controller.requestobj.AuthenticationRequest;
import com.projectpal.controller.requestobj.RegisterRequest;
import com.projectpal.controller.responseobj.AuthenticationResponse;
import com.projectpal.entity.User;
import com.projectpal.entity.enums.Role;
import com.projectpal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	@Autowired
	private final UserRepository repo;
	@Autowired
	private final PasswordEncoder passwordEncoder;
	@Autowired
	private final JwtService jwtService;
	@Autowired
	private final AuthenticationManager authManager;

	public AuthenticationResponse register(RegisterRequest req) {
		
		
		User user = new User(req.getName(), req.getEmail(), passwordEncoder.encode(req.getPassword()), Role.USER,
				null);
		
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
