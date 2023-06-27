package com.projectpal.dto.response;

public final class AuthenticationResponse {

	public AuthenticationResponse(String token) {
		this.token = token;
	}

	private final String token;

	public String getToken() {
		return token;
	}

}
