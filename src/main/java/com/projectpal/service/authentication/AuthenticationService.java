package com.projectpal.service.authentication;

import com.projectpal.dto.request.authentication.AuthenticationRequest;
import com.projectpal.dto.request.authentication.RegisterRequest;
import com.projectpal.dto.response.AuthenticationResponse;

public interface AuthenticationService {

	public AuthenticationResponse register(RegisterRequest req);

	public AuthenticationResponse authenticate(AuthenticationRequest req);
}
